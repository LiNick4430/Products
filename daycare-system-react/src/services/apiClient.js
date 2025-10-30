// services/authService.js

const API_BASE_URL = "http://localhost:8080";

/**
 * 建立一個通用的 API 請求客戶端。
 * 它會自動處理錯誤、JSON 解析，並可選地加入 Access Token。
 * @param {string} url - 請求的路徑 (不包含 BASE_URL)。
 * @param {string} method - 請求方法 (預設'GET') ('GET', 'POST', 'PUT', 'DELETE')。
 * @param {object} [data=null] - 請求體 (預設 Null) (適用於 POST/PUT/DELETE)。
 * @param {boolean} [requiresAuth=false] - 是否需要 Access Token (預設 false)。
 * @returns {Promise<Object>} - 伺服器響應的資料物件。
 * @throws {Error} - 拋出包含錯誤訊息或狀態碼的錯誤。
 */

export async function request(url, method = 'GET', data = null, requiresAuth = false) {
  // 1. 取得當前儲存的 Access Token
  const accessToken = localStorage.getItem("accessToken");

  // 2. 建立 HEADER
  const headers = {
    "Content-Type": "application/json",
  };

  // 3. 判斷是否需要認證 (TOKEN 注入)
  if (requiresAuth) {
    if (!accessToken) {
      // 發現缺少 Access Token 時，立即在前端拋出錯誤，無需連線後端
      throw new Error("AUTH_REQUIRED: 缺少 Access Token，請先登入。");
    }
    headers['Authorization'] = `Bearer ${accessToken}`;
  }

  // 4. 建立請求配置
  const config = {
    method: method,
    headers: headers,
    credentials: 'include',
  }

  if (data) {
    config.body = JSON.stringify(data);
  }

  // 5. FETCH
  const response = await fetch(`${API_BASE_URL}${url}`, config);

  // 6. 處理網路層面或業務層面的失敗 (如果 HTTP 狀態碼不是 2xx)
  if (!response.ok) {
    // 嘗試解析錯誤訊息，後端有返回錯誤JSON
    try {
      const errorData = await response.json();
      // 精準關鍵字 是 讓 後續更容易觸發 刷新 TOKEN
      if (errorData.errorCode && errorData.errorCode.includes("JWT_EXPIRED")) {
        throw new Error("JWT_EXPIRED"); // 拋出精確關鍵字
      }
      // 拋出後端返回的 message (例如："帳號或密碼錯誤")
      throw new Error(`錯誤類型:${errorData.errorCode}, 錯誤訊息:${errorData.message}`);
    } catch (e) {
      // 如果解析 JSON 失敗
      throw new Error(`網路錯誤，無法連接伺服器或解析錯誤訊息。狀態碼: ${response.status}`);
    }
  }

  // 7. 如果 HTTP 狀態碼是 2xx (登入成功)
  // 假設登入成功時，後端會返回包含 Token 的 ApiResponse (code: 200)
  const responseData = await response.json();

  // 雖然已經是 2xx，但我們仍然可以做一個最終的業務碼檢查 (以防萬一)
  if (responseData.code !== 200) {
    throw new Error(responseData.message || "登入成功後，業務碼異常。");
  }

  return responseData;
}