import React from "react";

const HomePage = () => {
    return (
        <div className="home-page">
            <header className="home-header">
                <h1>ByBud</h1> {/* Logo */}
            </header>
            <section className="home-content">
                <h2>Velkommen til ByBud</h2>
                <p>
                    ByBud
                    – en moderne løsning med røtter i byens historie.
                    Vi tilbyr pålidelig og effektiv leveringstjeneste,
                    der vi finnerde bedste budene i byen.
                    Med oss kan du sende en pakke eller få
                    hentet en.
                    vi er ByBud din ideelle partner.
                </p>
                <div className="home-buttons">
                    <a href="/login" className="btn btn-primary">Login</a>
                    <a href="/register" className="btn btn-secondary">Registrer</a>
                </div>
            </section>
        </div>
    );
};

export default HomePage;
