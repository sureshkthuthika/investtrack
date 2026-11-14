
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import portfolioService from "../services/portfolioService";

function PortfolioDetails() {
    const { portfolioId } = useParams();
    const navigate = useNavigate();

    const [holdings, setHoldings] = useState([]);
    const [transactions, setTransactions] = useState([]);

    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    // Transaction form fields
    const [symbol, setSymbol] = useState("");
    const [type, setType] = useState("BUY");
    const [quantity, setQuantity] = useState("");
    const [price, setPrice] = useState("");

    // Load holdings and transaction history
    const loadDetails = async () => {
        try {
            setError("");

            const [holdingsData, transactionsData] = await Promise.all([
                portfolioService.getHoldings(portfolioId),
                portfolioService.getTransactions(portfolioId)
            ]);

            setHoldings(holdingsData);
            setTransactions(transactionsData);
        } catch (err) {
            console.error("Portfolio details error:", err);

            setError(
                err.response?.data?.message ||
                "Unable to load portfolio details."
            );
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadDetails();
    }, [portfolioId]);

    // Submit BUY or SELL transaction
    const handleTransaction = async (event) => {
        event.preventDefault();

        try {
            setSubmitting(true);
            setError("");
            setSuccess("");

            await portfolioService.addTransaction(portfolioId, {
                symbol: symbol.trim().toUpperCase(),
                type,
                quantity: Number(quantity),
                price: Number(price)
            });

            setSuccess(`${type} transaction completed successfully.`);

            setSymbol("");
            setType("BUY");
            setQuantity("");
            setPrice("");

            await loadDetails();
        } catch (err) {
            console.error("Transaction error:", err);

            setError(
                err.response?.data?.message ||
                "Unable to process transaction."
            );
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) {
        return <p>Loading portfolio details...</p>;
    }

    return (
        <div className="dashboard-container">

            <button onClick={() => navigate("/dashboard")}>
                Back to Dashboard
            </button>

            <h1>Portfolio Details</h1>

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

            {/* Add Transaction Form */}
            <section className="portfolio-section">

                <h2>Add Transaction</h2>

                <form
                    className="portfolio-form"
                    onSubmit={handleTransaction}
                >
                    <label>Stock Symbol</label>

                    <input
                        type="text"
                        value={symbol}
                        onChange={(event) =>
                            setSymbol(event.target.value)
                        }
                        placeholder="Example: AAPL"
                        required
                    />

                    <label>Transaction Type</label>

                    <select
                        value={type}
                        onChange={(event) =>
                            setType(event.target.value)
                        }
                    >
                        <option value="BUY">BUY</option>
                        <option value="SELL">SELL</option>
                    </select>

                    <label>Quantity</label>

                    <input
                        type="number"
                        min="0.000001"
                        step="any"
                        value={quantity}
                        onChange={(event) =>
                            setQuantity(event.target.value)
                        }
                        required
                    />

                    <label>Price Per Share ($)</label>

                    <input
                        type="number"
                        min="0.000001"
                        step="any"
                        value={price}
                        onChange={(event) =>
                            setPrice(event.target.value)
                        }
                        required
                    />

                    <button type="submit" disabled={submitting}>
                        {submitting
                            ? "Processing..."
                            : `Submit ${type}`}
                    </button>

                </form>

            </section>

            {/* Holdings */}
            <section className="portfolio-section">

                <h2>Holdings</h2>

                {holdings.length === 0 ? (
                    <p>No holdings found.</p>
                ) : (
                    <table>
                        <thead>
                            <tr>
                                <th>Symbol</th>
                                <th>Quantity</th>
                                <th>Average Cost</th>
                            </tr>
                        </thead>

                        <tbody>
                            {holdings.map((holding) => (
                                <tr key={holding.id}>
                                    <td>{holding.symbol}</td>
                                    <td>{holding.quantity}</td>
                                    <td>
                                        ${Number(holding.averageCost).toFixed(2)}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}

            </section>

            {/* Transaction History */}
            <section className="portfolio-section">

                <h2>Transaction History</h2>

                {transactions.length === 0 ? (
                    <p>No transactions found.</p>
                ) : (
                    <table>
                        <thead>
                            <tr>
                                <th>Symbol</th>
                                <th>Type</th>
                                <th>Quantity</th>
                                <th>Price</th>
                                <th>Total</th>
                            </tr>
                        </thead>

                        <tbody>
                            {transactions.map((transaction) => (
                                <tr key={transaction.id}>
                                    <td>{transaction.symbol}</td>
                                    <td>{transaction.type}</td>
                                    <td>{transaction.quantity}</td>
                                    <td>
                                        ${Number(transaction.price).toFixed(2)}
                                    </td>
                                    <td>
                                        ${Number(transaction.totalAmount).toFixed(2)}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}

            </section>

        </div>
    );
}

export default PortfolioDetails;
