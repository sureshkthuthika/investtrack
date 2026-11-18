import { Client } from "@stomp/stompjs";

export function connectToPortfolio(portfolioId, onUpdate) {
    const token = localStorage.getItem("token");

    if (!token || token === "undefined" || token === "null") {
        throw new Error("JWT token not found. Please log in.");
    }

    const client = new Client({
        brokerURL:
            import.meta.env.VITE_WS_URL || "ws://localhost:8080/ws",

        connectHeaders: {
            Authorization: `Bearer ${token}`
        },

        reconnectDelay: 5000,

        onConnect: () => {
            console.log("WebSocket connected");

            client.subscribe(
                `/topic/portfolios/${portfolioId}`,
                (message) => {
                    try {
                        const event = JSON.parse(message.body);
                        console.log("Portfolio update received:", event);
                        onUpdate(event);
                    } catch (error) {
                        console.error("Invalid WebSocket message:", error);
                    }
                }
            );
        },

        onStompError: (frame) => {
            console.error("STOMP error:", frame.headers.message);
        },

        onWebSocketError: (error) => {
            console.error("WebSocket error:", error);
        }
    });

    client.activate();

    return () => {
        client.deactivate();
    };
}


export function connectToUserUpdates(onUpdate) {
    const token = localStorage.getItem("token");

    if (!token || token === "undefined" || token === "null") {
        throw new Error("JWT token not found. Please log in.");
    }

    const client = new Client({
        brokerURL:
            import.meta.env.VITE_WS_URL || "ws://localhost:8080/ws",

        connectHeaders: {
            Authorization: `Bearer ${token}`
        },

        reconnectDelay: 5000,

        onConnect: () => {
            console.log("User notification WebSocket connected");

            client.subscribe(
                "/user/queue/portfolio-updates",
                (message) => {
                    try {
                        const event = JSON.parse(message.body);
                        console.log("Dashboard update received:", event);
                        onUpdate(event);
                    } catch (error) {
                        console.error("Invalid WebSocket message:", error);
                    }
                }
            );
        },

        onStompError: (frame) => {
            console.error("STOMP error:", frame.headers.message);
        },

        onWebSocketError: (error) => {
            console.error("WebSocket error:", error);
        }
    });

    client.activate();

    return () => {
        client.deactivate();
    };
}

