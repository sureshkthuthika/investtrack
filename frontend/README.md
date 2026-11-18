# InvestTrack Frontend

## Overview

InvestTrack is a React-based investment portfolio management application. It provides a dashboard to manage portfolios, holdings, and investment transactions with real-time updates.

## Technology Stack

- React
- JavaScript
- Vite
- Axios
- React Router
- WebSocket (STOMP)
- npm

## Prerequisites

- Node.js
- npm
- InvestTrack Spring Boot backend running

## Installation

Clone the repository and navigate to the frontend directory.

```powershell
cd frontend
npm install
```

## Run Application

```powershell
npm run dev
```

Application URL:

http://localhost:5173

## Production Build

```powershell
npm run build
```

Preview production build:

```powershell
npm run preview
```

## Features

- User registration and login
- JWT-based authentication
- Portfolio dashboard
- Create, edit, and delete portfolios
- View portfolio details
- Add, edit, and delete holdings
- BUY and SELL transactions
- Transaction history
- Real-time portfolio updates using WebSocket

## Backend Integration

Backend API:

http://localhost:8080

The frontend communicates with the Spring Boot backend through REST APIs secured with JWT authentication.

## Project Structure

```text
frontend/
├── public/
├── src/
│   ├── pages/
│   │   ├── Dashboard.jsx
│   │   └── PortfolioDetails.jsx
│   ├── services/
│   │   ├── api.js
│   │   ├── portfolioService.js
│   │   └── websocketService.js
│   ├── App.jsx
│   └── main.jsx
├── package.json
├── package-lock.json
└── README.md
```

## Development Tools

- Vite provides fast development and Hot Module Replacement (HMR).
- ESLint is used for code quality.
- React Router handles application navigation.

## React and Vite References

- [React Documentation](https://react.dev/)
- [Vite Documentation](https://