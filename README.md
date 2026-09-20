# Banking security practice

This project supplies a complete Spring Boot/MySQL banking backend and a React frontend. Students complete three authorization blocks in:

`backend/src/main/java/com/example/banking/config/SecurityConfiguration.java`

Read **Lesson_04_Security_Ticket_Breakdown.md** for the tasks and acceptance checks. The starter's missing permission rules deliberately leave banking endpoints inaccessible. The instructor solution contains those rules. Everything else in the application is the same.

## Run

1. Use MySQL 8, JDK 17 or 21, and Node 22.12 or newer.
2. Run **sql/Banking_Setup.sql** in MySQL Workbench. It creates the database and every required table. It preserves existing records.
3. Open **backend/pom.xml** in IntelliJ as a Maven project. Set your MySQL username/password in **backend/src/main/resources/application.properties**. Keep database **banking_app** and **ddl-auto=validate**.
4. Run **com.example.banking.BankingApplication** on port **8080**. Stop any other backend on that port.
5. From the **frontend** folder:

```bash
npm install
npm run dev
```

Open **http://localhost:5173**. The Vite proxy sends `/api` calls to the backend. Use the complete frontend folder so every component/import is present. Both projects' dependencies are pinned in their build files/lockfile. You may use `npm ci` instead of `npm install` for an exact lockfile install.

With Maven installed, an alternative backend command from the backend folder is `mvn spring-boot:run`. IntelliJ can use its bundled Maven, so a separate Maven installation is optional.

## Demo users

| Email | Password | Role |
| --- | --- | --- |
| casey.bank@example.test | BankDemo!2026 | CUSTOMER |
| jordan.bank@example.test | BankDemo!2026 | CUSTOMER |
| admin.bank@example.test | BankDemo!2026 | ADMIN |

The supplied **config/DemoData.java** creates these local demo fixtures on first startup. PasswordEncoder generates their BCrypt hashes. Existing users, roles, balances and transactions are not reset by restarting the server. Registration creates ordinary CUSTOMER accounts with two zero-balance bank accounts.

This is a classroom simulation with fictional money. Deposits represent simulated cash deposits; no payment processor or real bank is connected. Administrators review and freeze accounts. They cannot submit personal deposits, withdrawals or transfers with ADMIN authority alone.

## Project files

| Location | Purpose |
| --- | --- |
| backend/.../config/SecurityConfiguration.java | The only Java file students edit. |
| backend/.../models | AppUser, BankAccount and BankTransaction. |
| backend/.../repositories | Account lookup and transaction history. |
| backend/.../services | Registration, stored login credentials and banking operations. |
| backend/.../controllers | Auth, public information, account, transfer and admin endpoints. |
| frontend/src/components | Header, login form, account cards, money forms, transaction history and admin account table. |
| frontend/src/api.js and auth.js | Session requests and CSRF handling. |
| sql/Banking_Setup.sql | Complete non-destructive schema setup. |

## Common issues

- **Login works, but banking operations show access denied in the starter:** complete the relevant matcher block. This is expected until the exercise is finished.
- **Unknown database or missing table:** run Banking_Setup.sql in full. Keep ddl-auto=validate.
- **Connection rejected:** use your own local MySQL password in application.properties.
- **Port already in use:** stop the previous ecommerce app or frontend.
- **Session disappeared after editing:** a backend restart invalidates local sessions. Sign in again.
- **Role changed in MySQL but the interface did not change:** sign out and sign back in. A new login reloads authorities.
- **Frozen balance differs from what you remember:** Freeze preserves funds. Refresh to load the most recent state.

## File I/O continuation

Keep the database and the supplied entities. BankTransaction contains the transaction ID, account reference, positive amount, transaction type, description, resulting balance, UTC timestamp and operation reference. The two sides of a transfer share a reference. The history is ordered by transaction ID, newest first. File I/O can use this persisted data for a statement without replacing the banking model.

Successful balance changes and history inserts commit together. Failed operations roll back. Services use database row locks so concurrent operations cannot spend the same balance twice. This infrastructure is supplied, not additional work for the security exercise.

Money amounts use BigDecimal with two decimal places. There is no endpoint to overwrite an account balance directly or delete financial history. Demo logs are written to **logs/banking.log**, relative to the backend's working directory.

## Northbank blue interface

The included React frontend is the blue **NORTHBANK / Banking portal** interface. Customers see an account sidebar, money movement forms, and transaction history. Administrators see customer accounts with Freeze and Reactivate actions. This frontend contains no shopping or cart screens.

Stop the previous Vite process with Ctrl+C before starting this frontend. Open a terminal in this ZIP's **banking-security/frontend** folder, then run npm install and npm run dev. The header should say NORTHBANK. The backend must be **com.example.banking.BankingApplication**, connected to **banking_app**.

For a complete instructor demonstration, run the **solution** backend. The starter already supports registration, login and logout, but its unfinished request matchers deliberately block the banking routes until students complete the security tickets.

### Existing banking features

- Registration automatically opens one checking and one savings account at $0. There is no separate form to open additional accounts.
- Customers can make simulated deposits and withdrawals, transfer between their own accounts, and read their saved transaction history.
- Invalid amounts, overdrafts and money movements on frozen accounts are rejected.
- Customers can access only their own account data. Administrators can review all accounts and freeze or reactivate them; they do not use customer money movement controls.
- Transfers update both balances and create matching transaction entries together. Data persists in MySQL.

This is useful for teaching login, role-based permissions and account ownership with visible results. The existing transaction history supplies the data for the next File I/O statement exercise. PDF/CSV statement export and transfers to another customer are not implemented in this version.
