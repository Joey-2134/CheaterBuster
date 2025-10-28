import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminLogin.css';

function AdminLogin() {
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();

    if (!password) {
      setError('Please enter a password');
      return;
    }

    // Store the API key (password) in sessionStorage
    // sessionStorage clears when the browser tab is closed
    sessionStorage.setItem('adminApiKey', password);

    // Navigate to admin dashboard
    navigate('/admin/dashboard');
  };

  return (
    <div className="admin-login-container">
      <div className="admin-login-card">
        <h1>Admin Access</h1>
        <p className="admin-login-subtitle">Enter your API key to continue</p>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="password">API Key</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                setError(''); // Clear error when typing
              }}
              placeholder="Enter your API key"
              autoFocus
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <button type="submit" className="login-button">
            Access Admin Panel
          </button>
        </form>

        <button
          className="back-button"
          onClick={() => navigate('/')}
        >
          Back to Home
        </button>
      </div>
    </div>
  );
}

export default AdminLogin;
