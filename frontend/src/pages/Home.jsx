import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getPlayerCount } from "../request/gathering.js";
import './Home.css';

function Home() {
  const [count, setCount] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    getPlayerCount()
      .then(data => setCount(data))
      .catch(err => console.error('Failed to fetch count:', err));
  }, []);

  return (
    <div className="home-container">
      <div className="admin-link">
        <button onClick={() => navigate('/admin')} className="admin-button">
          Admin Panel
        </button>
      </div>

      <h1>CheaterBuster</h1>
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>
          <p>This model has been trained on</p>
          {count}
          <p>different players!</p>
        </button>
      </div>
    </div>
  );
}

export default Home;
