import { Link } from "react-router-dom"

const NotFoundPage = () => {
  return (
    <>
        <div>Page introuvable</div>
        <Link to={"/"}>
            <button>Retour à l'accueil</button>
        </Link>
    </>
  )
}

export default NotFoundPage