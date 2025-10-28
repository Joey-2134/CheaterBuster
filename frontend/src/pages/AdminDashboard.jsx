import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getGatheringStatus,
  startGathering,
  stopGathering
} from '../request/admin.js';
import StatusCard from '../components/admin/StatusCard.jsx';
import ControlCard from '../components/admin/ControlCard.jsx';
import StatsCard from '../components/admin/StatsCard.jsx';
import './AdminDashboard.css';

function AdminDashboard() {
  const navigate = useNavigate();
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);

  const loadStatus = async () => {
    try {
      setLoading(true);
      setError('');
      const statusData = await getGatheringStatus();
      setStatus(statusData);
    } catch (err) {
      console.error('Failed to fetch status:', err);
      if (err.message.includes('401') || err.message.includes('Invalid API key')) {
        setError('Invalid API key. Please log in again.');
        setTimeout(() => {
          handleLogout();
        }, 2000);
      } else {
        setError('Failed to load gathering status: ' + err.message);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStatus();
    // Poll status every 5 seconds
    const interval = setInterval(loadStatus, 5000);
    return () => clearInterval(interval);
  }, []);

  const handleStart = async (mode, batchSize) => {
    try {
      setActionLoading(true);
      setError('');
      await startGathering(mode, batchSize);
      await loadStatus();
    } catch (err) {
      console.error('Failed to start gathering:', err);
      if (err.message.includes('401') || err.message.includes('Invalid API key')) {
        setError('Invalid API key. Please log in again.');
        setTimeout(() => {
          handleLogout();
        }, 2000);
      } else {
        setError('Failed to start gathering: ' + err.message);
      }
    } finally {
      setActionLoading(false);
    }
  };

  const handleStop = async () => {
    try {
      setActionLoading(true);
      setError('');
      await stopGathering();
      await loadStatus();
    } catch (err) {
      console.error('Failed to stop gathering:', err);
      if (err.message.includes('401') || err.message.includes('Invalid API key')) {
        setError('Invalid API key. Please log in again.');
        setTimeout(() => {
          handleLogout();
        }, 2000);
      } else {
        setError('Failed to stop gathering: ' + err.message);
      }
    } finally {
      setActionLoading(false);
    }
  };

  const handleLogout = () => {
    sessionStorage.removeItem('adminApiKey');
    navigate('/admin');
  };

  if (loading && !status) {
    return (
      <div className="admin-dashboard">
        <div className="loading">Loading...</div>
      </div>
    );
  }

  return (
    <div className="admin-dashboard">
      <div className="dashboard-header">
        <h1>Admin Dashboard</h1>
        <div className="header-actions">
          <button className="home-button" onClick={() => navigate('/')}>
            Home
          </button>
          <button className="logout-button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <StatusCard status={status} />
      <ControlCard
        status={status}
        onStart={handleStart}
        onStop={handleStop}
        actionLoading={actionLoading}
      />
      <StatsCard/>
    </div>
  );
}

export default AdminDashboard;
