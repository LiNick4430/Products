import React from "react";
import { Link } from "react-router-dom";
import "./Navbar.css"; // 引入樣式

function Navbar({ username, isLoggedIn, onLogout }) {
  // 1. 顯示名稱
  const displayUsername = isLoggedIn ? username : "遊客";

  // 2. 組合 問候語
  const greeting = `${displayUsername} 您好`;

  return (
    <nav className="navbar">
      <h2 className="navbar-title">{greeting}</h2>
      <ul className="navbar-links">
        <li>
          <Link to="/"><button>首頁</button></Link>
        </li>

        {isLoggedIn ? (
          <>
            <button onClick={onLogout}>
              登出
            </button>
          </>
        ) : (
          <>
            <li>
              <Link to="login">
                <button>
                  登入
                </button>
              </Link>
            </li>
          </>
        )}

      </ul>
    </nav>
  );
}

export default Navbar;