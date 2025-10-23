import { useState } from 'react'
import './App.css'
import Navbar from "./components/NavBar"
import Footer from "./components/Footer"
import Home from "./pages/Home"
import {
  BrowserRouter as Router,
  Routes,
  Route,
} from "react-router-dom";

function App() {

  return (
    <Router>
      {/* 上方 導覽列 */}
      <Navbar />

      {/* 中間 內容 */}
      <div className='content'>
        <Routes>
          {/* 首頁路由 */}
          <Route path="/" element={<Home />} />
        </Routes>
      </div>

      {/* 下方 頁尾 */}
      <Footer />
    </Router>
  )
}

export default App
