// services/authService.js

const API_BASE_URL = "http://localhost:8080";

/**
 * 一個通用的 API 請求客戶端。
 * 自動處理錯誤、JSON 解析，可選加入 Access Token。
 * @param {string} url - 請求的路徑 (不包含 BASE_URL)。
 * @param {string} method - 請求方法 (預設'GET') ('GET', 'POST', 'DELETE')。
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

  // 6. 嘗試讀取 Response Body
  let responseData = null;
  try {
    // 嘗試將 Response Body 讀取為 JSON
    // 注意：如果 Body 為空 (例如 204 No Content)，response.json() 會拋錯
    responseData = await response.json();
  } catch (e) {
    // 如果讀取 JSON 失敗 (e.g., Body 是空的或不是 JSON)，responseData 會保持 null
    // 這是預期行為，我們讓 responseData 保持 null 或 log 警告，繼續流程
    console.warn(`URL: ${url} Response Body 無法解析為 JSON 或為空。`, e);
  }

  // 7. 處理網路層面或業務層面的失敗 (如果 HTTP 狀態碼不是 2xx)
  if (!response.ok) {
    // 優先使用後端返回的業務錯誤訊息
    if (responseData && responseData.message) {

      // 處理 JWT 過期等需要精準關鍵字觸發刷新的錯誤
      if (responseData.errorCode && responseData.errorCode.includes("JWT_EXPIRED")) {
        throw new Error("JWT_EXPIRED"); // 拋出精確關鍵字，用於 AuthContext 刷新 Token
      }

      // 其他非 JWT_EXPIRED 的錯誤
      throw new Error(`錯誤類型：${responseData.errorCode}，錯誤訊息：${responseData.message}`);

    } else {
      // 如果 HTTP 狀態碼不是 2xx，但 Response Body 無法解析或沒有 message
      // 拋出您看到的通用錯誤，但現在它只會在後端沒有提供 JSON 錯誤體時才會觸發
      throw new Error(`網路錯誤，無法連接伺服器或解析錯誤訊息。狀態碼: ${response.status}`);
    }
  }

  // 8. 如果 HTTP 狀態碼是 2xx (成功)
  // 檢查業務碼 (code: 200)
  if (responseData.code !== 200) {
    throw new Error(responseData.message || "登入成功後，業務碼異常。");
  }

  return responseData;
}