import { Navigate } from 'react-router-dom';

/**
 * ProtectedRoute component that checks if user has an API key in session storage
 * Redirects to admin login if no API key is found
 */
function ProtectedRoute({ children }) {
  const apiKey = sessionStorage.getItem('adminApiKey');

  if (!apiKey) {
    return <Navigate to="/admin" replace />;
  }

  return children;
}

export default ProtectedRoute;
