import React from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext"
import "./Navbar.css"; // 引入樣式

function Navbar() {
  const { username, isLoggedIn, handleLogout, roleName } = useAuth();

  // 1. 顯示名稱
  const displayUsername = isLoggedIn ? username : "遊客";

  // 2. 組合 問候語
  const greeting = `政府公托系統 ${displayUsername} 您好`;

  return (
    <nav className="navbar">
      <h2 className="navbar-title">{greeting}</h2>
      <ul className="navbar-links">
        <li>
          <Link to="/"><button>首頁</button></Link>
        </li>

        {isLoggedIn ? (
          <>
            {/* ------------------- 已登入 ------------------- */}

            {/* 管理者 的 連接區域 */}
            {roleName === "ROLE_MANAGER" && (
              <>
                <li><Link to="/"><button>帳號管理</button></Link></li>  {/* 前後台 */}

                <li><Link to="/"><button>公告管理</button></Link></li>
                <li><Link to="/"><button>規範說明</button></Link></li>

                <li><Link to="/"><button>機構管理</button></Link></li>
                <li><Link to="/"><button>班級管理</button></Link></li>

                <li><Link to="/"><button>申請審核</button></Link></li>
                <li><Link to="/"><button>徹銷審核</button></Link></li>
                <li><Link to="/"><button>補位抽籤</button></Link></li>
                <li><Link to="/"><button>候補清冊</button></Link></li>
                <li><Link to="/"><button>個案管理</button></Link></li>
              </>
            )}

            {/* 基層人員 的 連接區域 */}
            {roleName === "ROLE_STAFF" && (
              <>
                <li><Link to="/"><button>公告管理</button></Link></li>

                <li><Link to="/"><button>機構管理</button></Link></li>
                <li><Link to="/"><button>班級管理</button></Link></li>

                <li><Link to="/"><button>申請審核</button></Link></li>
                <li><Link to="/"><button>徹銷審核</button></Link></li>
                <li><Link to="/"><button>候補清冊</button></Link></li>
                <li><Link to="/"><button>個案管理</button></Link></li>
              </>
            )}

            {/* 民眾 的 連接區域 */}
            {roleName === "ROLE_PUBLIC" && (
              <>
                <li><Link to="/child/information"><button>幼兒資料</button></Link></li>
                <li><Link to="/document/public/information"><button>附件管理</button></Link></li>
                <li><Link to="/case/public/information"><button>線上申辦</button></Link></li>
              </>
            )}

            {/* 通用 的 連接區域 */}
            <li><Link to="/user/me"><button>個人資料</button></Link></li>
            <li><button onClick={handleLogout}>登出</button></li>
          </>
        ) : (
          <>
            {/* ------------------- 未登入-------------------  */}

            <li><Link to="/register"><button>註冊</button></Link></li>
            <li><Link to="/login"><button>登入</button></Link></li>
          </>
        )}

      </ul>
    </nav>
  );
}

export default Navbar;