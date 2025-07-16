import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { API_BASE_URL, CALENDAR_API } from '../config/constants';

const CalendarTab: React.FC = () => {
  const { token } = useAuth();
  const [response, setResponse] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchCalendar = async () => {
    setLoading(true);
    setError(null);
    setResponse(null);
    try {
      const res = await fetch(`${API_BASE_URL}${CALENDAR_API.events}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      if (!res.ok) {
        throw new Error('Failed to fetch calendar events');
      }
      const data = await res.json();
      setResponse(data);
    } catch (err: any) {
      setError(err.message || 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Calendar</h2>
      <button onClick={fetchCalendar} disabled={loading || !token}>
        {loading ? 'Loading...' : 'Fetch Calendar Events'}
      </button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {response && (
        <pre style={{ textAlign: 'left', background: '#f4f4f4', padding: '1em' }}>
          {JSON.stringify(response, null, 2)}
        </pre>
      )}
      {!token && <p style={{ color: 'orange' }}>Please login to fetch calendar events.</p>}
    </div>
  );
};

export default CalendarTab; 