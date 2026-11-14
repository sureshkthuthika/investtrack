
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import authService from "./services/authService";
import PortfolioDetails from "./pages/PortfolioDetails";

function ProtectedRoute({ children }) {
    return authService.isAuthenticated()
        ? children
        : <Navigate to="/" replace />;
}

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Login />} />

                <Route path="/register" element={<Register />} />

                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>
                            <Dashboard />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/portfolios/:portfolioId"
                    element={
                        <ProtectedRoute>
                            <PortfolioDetails />
                        </ProtectedRoute>
                    }
                />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
