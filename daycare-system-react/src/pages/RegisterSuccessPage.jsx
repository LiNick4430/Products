import { useNavigate } from 'react-router-dom';

function RegisterSuccessPage() {
  const navigate = useNavigate();

  return (
    <div style={{ padding: '50px', textAlign: 'center' }}>
      <h2>註冊成功！</h2>
      <p>感謝您的註冊。我們已發送一封帳號啟用信件至您的電子信箱。</p>
      <p style={{ fontWeight: 'bold', color: 'red' }}>請前往您的信箱，點擊信件中的連結以完成帳號啟用，然後再返回登入頁面。</p>
      <button
        onClick={() => navigate('/login')}
        style={{ marginTop: '20px', padding: '10px 20px' }}>
        前往登入頁
      </button>
    </div>
  );
}

export default RegisterSuccessPage;