package com.seaside.seaside_api.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seaside.seaside_api.dto.request.LoginRequest;
import com.seaside.seaside_api.dto.request.RegisterRequest;
import com.seaside.seaside_api.dto.response.AuthResponse;
import com.seaside.seaside_api.entity.RefreshToken;
import com.seaside.seaside_api.entity.Utilisateur;
import com.seaside.seaside_api.entity.enums.RoleUsers;
import com.seaside.seaside_api.repository.UtilisateurRepository;
import com.seaside.seaside_api.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEnconEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    //  ----------  Inscription --------------------
    public AuthResponse inscrire(RegisterRequest request) {
            
        // Verifier si email deja utilise
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est deja utilise");
        }

        // Verifier si nom d'utilisateur deja utilise
        if (utilisateurRepository.existsByNomUtilisateur(request.getNomUtilisateur())) {
            throw new RuntimeException("Ce nom d'utilisateur est deja pris");
        }

        // Creer l'utilisateur avec mot de passe  hashe par BCrypt
        Utilisateur utilisateur = Utilisateur.builder()
                .nomUtilisateur(request.getNomUtilisateur())
                .email(request.getEmail())
                .motsDePasseHash(passwordEnconEncoder.encode(request.getMotDePasse()))// ← hash BCrypt
                .role(RoleUsers.CLIENT)
                .estActif(true)
                .build();

        utilisateurRepository.save(utilisateur);

        // Generer le token jwt
        String accessToken = jwtUtil.genererToken(utilisateur);
        RefreshToken refresh = refreshTokenService.creer(utilisateur);

        return AuthResponse.builder()
            .token(accessToken)
            .refreshToken(refresh.getToken())
            .type("Bearer")
            .email(utilisateur.getEmail())
            .nomUtilisateur(utilisateur.getNomUtilisateur())
            .role(utilisateur.getRole())
            .build();
    }

    //----------------   Connexion ---------------------
    public AuthResponse seConnecter(LoginRequest request) {

        // Spring security verifie email + mot de passe automatiquement
        // lance une exception si les credentiels sont incorrectes
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), 
                request.getMotDePasse()
            )
        );

        // Si on arrive ici , les credentials sont corrects
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String accesstoken = jwtUtil.genererToken(utilisateur);
        RefreshToken refresh = refreshTokenService.creer(utilisateur);

        return AuthResponse.builder()
            .token(accesstoken)
            .refreshToken(refresh.getToken())
            .type("Bearer")
            .email(utilisateur.getEmail())
            .nomUtilisateur(utilisateur.getNomUtilisateur())
            .role(utilisateur.getRole())
            .build();
    }

    // -----------Renouveler l'access token
    public AuthResponse rafraichir(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.valider(refreshTokenStr);
        Utilisateur utilisateur   = refreshToken.getUtilisateur();
        String nouvelAccessToken  = jwtUtil.genererToken(utilisateur);

        return AuthResponse.builder()
                .token(nouvelAccessToken)
                .refreshToken(refreshTokenStr) // refresh token reste le même
                .type("Bearer")
                .email(utilisateur.getEmail())
                .nomUtilisateur(utilisateur.getNomUtilisateur())
                .role(utilisateur.getRole())
                .build();
    }


    // Deconnexion
    public void seDeconnecter() {
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        refreshTokenService.supprimerParUtilisateur(utilisateur);
    }
}
