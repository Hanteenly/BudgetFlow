# BudgetFlow

I got tired of guessing where the money went, so I built a small app for that.

Not a bank. You create accounts, throw income and expenses into categories, set a monthly limit, and see what the month actually looked like. Transfers between your own accounts are there too — just moving money from one pocket to another.

It's a Java backend with a plain HTML/JS frontend sitting on top of PostgreSQL. I kept the UI boring on purpose. I wanted the data and the API to be the interesting part.

Live: [budgetflow-5pg5.onrender.com](https://budgetflow-5pg5.onrender.com)

(It's on Render's free tier, so the first open after a nap can take a minute.)

## What's inside

Spring Boot, JPA, Security, Postgres. Registration is open. Everything under `/api` asks for HTTP Basic. After login the browser remembers you for that tab only.

You get accounts, transactions, categories, budgets, and a couple of reports. Nothing fancy — if you can name a category and a month, you can see whether you stayed under the limit.

## Run it locally

Java 25, Maven, and a Postgres database called `budgetflow` (user `postgres` / `postgres` unless you change it).

```bash
./mvnw spring-boot:run
```

Then open [http://localhost:6060/](http://localhost:6060/). That's the app port, not the IntelliJ preview.

Or, if you prefer Docker:

```bash
docker compose up --build
```

Same URL.

## Deploy

There's a Dockerfile. On Render I just pointed a web service at this repo and gave it the Postgres `DATABASE_URL`. A push to `master` rebuilds it. Not instant — it cooks a new image.

---

I wrote this while learning Spring Boot. If something feels a bit raw, that's because it is. I still use it.
