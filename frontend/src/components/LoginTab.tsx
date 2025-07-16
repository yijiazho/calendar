import React, { useState } from 'react';
import { API_BASE_URL } from '../config/constants';

const LoginTab: React.FC = () => {
  const [loading, setLoading] = useState<'google' | 'outlook' | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleOAuthLogin = async (provider: 'google' | 'outlook') => {
    setLoading(provider);
    setError(null);
    try {
      const response = await fetch(`${API_BASE_URL}/auth/login/${provider}`);
      if (!response.ok) {
        throw new Error(`Failed to initiate ${provider} login`);
      }
      const data = await response.json();
      if (data && data.data) {
        // Redirect to the OAuth2 authorization URL provided by backend
        const redirectUrl = data.data.startsWith('http') ? data.data : `${API_BASE_URL}${data.data}`;
        window.location.href = redirectUrl; 
      } else {
        throw new Error('No redirect URL received');
      }
    } catch (err: any) {
      setError(err.message || 'Unknown error');
    } finally {
      setLoading(null);
    }
  };

  return (
    <div>
      <h2>Login</h2>
      <button onClick={() => handleOAuthLogin('google')} disabled={loading !== null}>
        {loading === 'google' ? 'Redirecting...' : 'Login with Google'}
      </button>
      <button onClick={() => handleOAuthLogin('outlook')} disabled={loading !== null} style={{ marginLeft: 10 }}>
        {loading === 'outlook' ? 'Redirecting...' : 'Login with Outlook'}
      </button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </div>
  );
};

export default LoginTab; 