import "./App.css";
import { Routes, Route } from "react-router-dom";
import Login from "./routes/login/Login.js";
import Join from "./routes/join/Join.js";
import Navigation from "./routes/navigation/navigation";
import Home from "./routes/home/home";
import DebateList from "./routes/debateList/DebateList";
import FreeDebateList from "./routes/debateList/FreeDebateList";
import DetailDebate from "./routes/DetailDebate/DetailDebate";
import DetailFree from "./routes/FreeDebate/DetailFree";
import AllDebate from "./routes/allDebate/AllDebate";
import Mypage from "./routes/Mypage/Mypage";

function App() {
  return (
    // <div className="App">

    <Routes>
      <Route path="/" element={<Navigation />}>
        <Route index={true} element={<Home />} />
        <Route path="login" element={<Login />} />
        <Route path="join" element={<Join />} />
        <Route path="allDebate" element={<AllDebate />} />
        <Route path="mypage" element={<Mypage />} />

        {/* <Route path="debateList" element={<DebateList />} /> */}
        {/*<Route path="debateList/:title" element={<DebateList />} />*/}
        <Route path="debateList/book/:booknum" element={<DebateList />} />
        <Route path="freeDebateList/:title" element={<FreeDebateList />} />
        {/* <Route path="detailDebate" element={<DetailDebate />} /> */}
        <Route
          path="detaildebate/:title/:topic/debateId/:debateId/book/:bookId"
          element={<DetailDebate />}
        />
        <Route path="detailfree/:title/:topic" element={<DetailFree />} />
      </Route>
    </Routes>

    // </div>
  );
}

export default App;
