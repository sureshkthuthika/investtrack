
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import portfolioService from "../services/portfolioService";
import authService from "../services/authService";
import { connectToUserUpdates } from "../services/websocketService";

function Dashboard() {
    const navigate = useNavigate();

    const [portfolios, setPortfolios] = useState([]);
    const [summaries, setSummaries] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    // Create portfolio form
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [creating, setCreating] = useState(false);
    const [editingPortfolioId, setEditingPortfolioId] = useState(null);
    const [editName, setEditName] = useState("");
    const [editDescription, setEditDescription] = useState("");
    const [savingEdit, setSavingEdit] = useState(false);

    // Load portfolios and their summaries
    const loadDashboard = async () => {
        try {
            setError("");

            const portfolioData = await portfolioService.getPortfolios();

            setPortfolios(portfolioData);

            const summaryData = await Promise.all(
                portfolioData.map(async (portfolio) => {
                    const portfolioId = portfolio.id ?? portfolio._id;

                    if (!portfolioId) {
                        return null;
                    }

                    try {
                        return await portfolioService.getPortfolioSummary(
                            portfolioId
                        );
                    } catch (err) {
                        console.error(
                            "Unable to load summary for portfolio:",
                            portfolio.name,
                            err
                        );
                        return null;
                    }
                })
            );

            setSummaries(summaryData);
        } catch (err) {
            console.error("Dashboard loading error:", err);

            setError(
                err.response?.data?.message ||
                "Unable to load dashboard."
            );
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadDashboard();

        const disconnect = connectToUserUpdates((event) => {
            console.log("Refreshing dashboard:", event.eventType);
            loadDashboard();
        });

        return () => disconnect();
    }, []);

    // Calculate dashboard totals
    const totalPortfolios = portfolios.length;

    const totalHoldings = summaries.reduce(
        (total, summary) =>
            total + (summary?.holdingsCount || 0),
        0
    );

    const totalCostBasis = summaries.reduce(
        (total, summary) =>
            total + Number(summary?.totalCostBasis || 0),
        0
    );

    // Create portfolio
    const handleCreatePortfolio = async (event) => {
        event.preventDefault();

        try {
            setCreating(true);
            setError("");
            setSuccess("");

            await portfolioService.createPortfolio({
                name: name.trim(),
                description: description.trim()
            });

            setName("");
            setDescription("");

            setSuccess("Portfolio created successfully.");

            await loadDashboard();
        } catch (err) {
            console.error("Portfolio creation error:", err);

            setError(
                err.response?.data?.message ||
                "Unable to create portfolio."
            );
        } finally {
            setCreating(false);
        }
    };

    // Start editing portfolio
const handleEditPortfolio = (event, portfolio) => {
    event.stopPropagation();

    const portfolioId = portfolio.id ?? portfolio._id;

    setEditingPortfolioId(portfolioId);
    setEditName(portfolio.name || "");
    setEditDescription(portfolio.description || "");
    setError("");
    setSuccess("");
};

// Save portfolio changes
const handleSavePortfolio = async (event, portfolioId) => {
    event.preventDefault();
    event.stopPropagation();

    try {
        setSavingEdit(true);
        setError("");
        setSuccess("");

        await portfolioService.updatePortfolio(portfolioId, {
            name: editName.trim(),
            description: editDescription.trim()
        });

        setEditingPortfolioId(null);
        setSuccess("Portfolio updated successfully.");

        await loadDashboard();
    } catch (err) {
        setError(
            err.response?.data?.message ||
            "Unable to update portfolio."
        );
    } finally {
        setSavingEdit(false);
    }
};

// Cancel editing
const handleCancelEdit = (event) => {
    event.stopPropagation();
    setEditingPortfolioId(null);
};

// Delete portfolio
const handleDeletePortfolio = async (event, portfolioId) => {
    event.stopPropagation();

    if (!window.confirm(
        "Are you sure you want to delete this portfolio?"
    )) {
        return;
    }

    try {
        setError("");
        setSuccess("");

        await portfolioService.deletePortfolio(portfolioId);

        setSuccess("Portfolio deleted successfully.");

        await loadDashboard();
    } catch (err) {
        setError(
            err.response?.data?.message ||
            "Unable to delete portfolio."
        );
    }
};

    // Logout
    const handleLogout = () => {
        authService.logout();
        navigate("/");
    };

    if (loading) {
        return (
            <div className="dashboard-container">
                <p>Loading dashboard...</p>
            </div>
        );
    }

    return (
        <div className="dashboard-container">

            {/* Dashboard Header */}
            <header className="dashboard-header">
                <div>
                    <h1>InvestTrack</h1>
                    <p>Investment Portfolio Management</p>
                </div>

                <button onClick={handleLogout}>
                    Logout
                </button>
            </header>

            <h2>Dashboard</h2>

            {/* Error and Success Messages */}
            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            {success && (
                <div className="success-message">
                    {success}
                </div>
            )}

            {/* Summary Cards */}
            <section className="summary-cards">

                <div className="summary-card">
                    <h3>Total Portfolios</h3>
                    <p>{totalPortfolios}</p>
                </div>

                <div className="summary-card">
                    <h3>Total Holdings</h3>
                    <p>{totalHoldings}</p>
                </div>

                <div className="summary-card">
                    <h3>Total Cost Basis</h3>
                    <p>
                        {totalCostBasis.toLocaleString("en-US", {
                            style: "currency",
                            currency: "USD"
                        })}
                    </p>
                </div>

            </section>

            {/* Create Portfolio */}
            <section className="portfolio-section">

                <h2>Create New Portfolio</h2>

                <form
                    className="portfolio-form"
                    onSubmit={handleCreatePortfolio}
                >
                    <label>Portfolio Name</label>

                    <input
                        type="text"
                        value={name}
                        onChange={(event) =>
                            setName(event.target.value)
                        }
                        placeholder="Example: Growth Portfolio"
                        required
                    />

                    <label>Description</label>

                    <textarea
                        value={description}
                        onChange={(event) =>
                            setDescription(event.target.value)
                        }
                        placeholder="Enter portfolio description"
                        required
                    />

                    <button type="submit" disabled={creating}>
                        {creating ? "Creating..." : "Create Portfolio"}
                    </button>
                </form>

            </section>

            {/* Portfolio List */}
            <section className="portfolio-section">

                <h2>My Portfolios</h2>

                {portfolios.length === 0 ? (
                    <p>No portfolios found. Create your first portfolio.</p>
                ) : (

                    <div className="portfolio-grid">

                        {portfolios.map((portfolio, index) => {
                            const portfolioId =
                                portfolio.id ?? portfolio._id;

                            const summary = summaries[index];

                            return (
                                <div
                                    className="portfolio-card"
                                    key={portfolioId || index}

                                    // Navigate to Portfolio Details
                                    onClick={() =>
                                        navigate(`/portfolios/${portfolioId}`)
                                    }

                                    style={{ cursor: "pointer" }}
                                >

                                    {editingPortfolioId === portfolioId ? (
                    <form
                        onSubmit={(event) =>
                            handleSavePortfolio(event, portfolioId)
                        }
                        onClick={(event) => event.stopPropagation()}
                    >
                        <label>Portfolio Name</label>

                        <input
                            value={editName}
                            onChange={(event) => setEditName(event.target.value)}
                            required
                        />

                        <label>Description</label>

                        <textarea
                            value={editDescription}
                            onChange={(event) =>
                                setEditDescription(event.target.value)
                            }
                            required
                        />

                        <button type="submit" disabled={savingEdit}>
                            {savingEdit ? "Saving..." : "Save"}
                        </button>

                        <button type="button" onClick={handleCancelEdit}>
                            Cancel
                        </button>
                    </form>
                ) : (
                    <>
                        <h3>{portfolio.name}</h3>

                        <p>{portfolio.description}</p>

                        <div className="portfolio-actions">
                            <button
                                type="button"
                                onClick={(event) =>
                                    handleEditPortfolio(event, portfolio)
                                }
                            >
                                Edit
                            </button>

                            <button
                                type="button"
                                onClick={(event) =>
                                    handleDeletePortfolio(event, portfolioId)
                                }
                            >
                                Delete
                            </button>
                        </div>
                    </>
                )}

                                    <p>
                                        <strong>Holdings:</strong>{" "}
                                        {summary?.holdingsCount ?? 0}
                                    </p>

                                    <p>
                                        <strong>Cost Basis:</strong>{" "}
                                        {Number(
                                            summary?.totalCostBasis || 0
                                        ).toLocaleString("en-US", {
                                            style: "currency",
                                            currency: "USD"
                                        })}
                                    </p>

                                    <p className="portfolio-link">
                                        View Portfolio Details →
                                    </p>

                                </div>
                            );
                        })}

                    </div>
                )}

            </section>

        </div>
    );
}

export default Dashboard;
