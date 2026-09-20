# Lesson 4 Ticket Breakdown: Secure the Banking Application

**Goal:** Configure who may access a working banking application's endpoints. Complete the three ticket blocks in **SecurityConfiguration.java**. The React frontend, database entities, banking services, login and registration are supplied.

**Java file to edit:**
`backend/src/main/java/com/example/banking/config/SecurityConfiguration.java`

Keep the supplied `SecurityFilterChain`, BCrypt encoder, session login/logout, CSRF settings and error handlers. Add the missing rules inside `authorizeHttpRequests`, above the existing `anyRequest().denyAll()` line. Database credentials in `application.properties` are the only setup values you need to change elsewhere.

## Start the supplied project

1. Extract **Lesson_04_Security_TB_Starter.zip**. The `banking-security` folder contains `backend`, `frontend` and `sql`.
2. In MySQL Workbench, run **sql/Banking_Setup.sql** in full. It creates the `banking_app` database and all three tables. It preserves existing records when run again.
3. Open `backend/pom.xml` as a Maven project in IntelliJ. Use JDK 17 or 21. In `backend/src/main/resources/application.properties`, set your MySQL username and password. Keep the database name `banking_app` and `spring.jpa.hibernate.ddl-auto=validate`.
4. Run **com.example.banking.BankingApplication**. Stop the lesson's ecommerce backend first so port 8080 is available. The first startup creates the demo users and opening balances below. Later startups do not reset them.
5. In a terminal inside `banking-security/frontend`, run:

```bash
npm install
npm run dev
```

Use Node 22.12 or newer. Open **http://localhost:5173**. Keep using `localhost` for the frontend. Restart the backend after editing Java, then sign in again because a backend restart ends the existing sessions.

### Demo logins

All three demo accounts use the password **BankDemo!2026**.

| Email | Role | Starting accounts on a fresh database |
| --- | --- | --- |
| casey.bank@example.test | CUSTOMER | Checking $500.00; savings $1,000.00. |
| jordan.bank@example.test | CUSTOMER | Checking $200.00; savings $300.00. |
| admin.bank@example.test | ADMIN | Account administration; no personal deposit accounts. |

These are fictional users and funds. DemoData creates the passwords as BCrypt hashes. Registration is also available and always creates a CUSTOMER with checking and savings balances of $0.00. It has no administrator role option.

**Expected starter behavior:** The backend compiles and starts. Registration, login, current-user lookup and logout work. Branch information and the banking screens are blocked until you add their permission rules. Access denied at this stage is part of the exercise.

## What the supplied banking code already does

- A customer sees only accounts belonging to their signed-in identity.
- Deposits and withdrawals change the balance and save a transaction record.
- Transfers move funds between the same customer's checking and savings accounts. Both balance changes and transaction records commit together.
- Invalid amounts and insufficient funds leave balances and transaction history unchanged.
- An administrator can review all accounts and freeze or reactivate them. Frozen accounts retain their history and reject deposits, withdrawals and transfers.

A request matcher checks permission to use an endpoint. The supplied service separately checks **which customer owns the account**. A CUSTOMER role does not permit accessing another customer's account by changing its ID.

## Ticket 1: Public information and protected account reads

**Purpose:** Allow visitors to see branch information while keeping bank-account data behind a login.

Inside the Ticket 1 section of `SecurityConfiguration.java`, add rules for:

| HTTP method | Path pattern | Required access |
| --- | --- | --- |
| GET | `/api/public/info` | Anyone, including visitors who have not signed in. |
| GET | `/api/accounts/**` | A signed-in CUSTOMER or ADMIN. |

### Tasks

1. Use `requestMatchers` with `HttpMethod.GET` so these rules apply to reads.
2. Use the public-access authorization method for branch information.
3. Use the role authorization method that accepts either CUSTOMER or ADMIN for account reads. Store/pass these role names without adding `ROLE_` yourself.
4. Keep the final deny-all rule in place. Do not allow all `/api/**` requests.

`/api/accounts/**` includes the account list, an account by ID, and its transaction history. The service returns only the signed-in owner's records. Admins use a separate endpoint to review everyone's accounts in Ticket 3.

### Acceptance checks through the frontend

- Signed out: the welcome page displays the branch support information.
- Sign in as Casey: checking, savings and the opening transaction are visible.
- Refresh the page: Casey remains signed in while the session is valid.
- Sign out and sign in as Jordan: Jordan sees their own balances, not Casey's.
- Deposit/withdrawal and admin operations remain blocked until their later tickets are complete.

## Ticket 2: Customer deposits, withdrawals and transfers

**Purpose:** Permit customers to move funds in their own accounts while leaving account administration protected.

In the Ticket 2 section, require the **CUSTOMER** role for these POST requests:

| HTTP method | Path pattern | Existing banking operation |
| --- | --- | --- |
| POST | `/api/accounts/*/deposits` | Add funds to the selected account. |
| POST | `/api/accounts/*/withdrawals` | Withdraw funds from the selected account. |
| POST | `/api/transfers` | Move funds between two accounts belonging to the signed-in customer. |

### Tasks

1. Use `HttpMethod.POST` with the listed patterns. You can group the three patterns in one matcher.
2. Require CUSTOMER using `hasRole`. The ADMIN role alone does not authorize personal money movements in this application.
3. Keep CSRF enabled. The supplied frontend attaches the token to each modifying request.
4. Leave ownership checks and balance calculations in the supplied services.

In these patterns, `*` matches the one path segment containing an account ID. `**` in Ticket 1 covers paths at multiple levels. For example, the concrete path `/api/accounts/1/deposits` matches the deposit pattern.

### Acceptance checks through the frontend

With Casey's checking account selected, complete this sequence. These balances assume the original demo data; if you have already used the account, apply the same changes to its current balance.

| Action | Expected result from the original balance |
| --- | --- |
| Deposit $50.00 with description `Cash deposit` | Checking becomes $550.00; a DEPOSIT appears in history. |
| Withdraw $20.00 | Checking becomes $530.00; a WITHDRAWAL appears. |
| Transfer $30.00 to Casey's savings | Checking becomes $500.00 and savings $1,030.00. Both accounts record the transfer. |
| Attempt to withdraw $9,999.00 | An insufficient-funds message appears. No balance or transaction changes. |
| Reload the page | The saved balances and transaction history remain. |

You do not need to create, update or delete transaction rows manually. This application preserves past transactions for later statements.

## Ticket 3: Administrator review and account status

**Purpose:** Allow bank staff to review accounts and control availability without giving customers those privileges.

In the Ticket 3 section, add these role rules:

| HTTP method | Path pattern | Required access |
| --- | --- | --- |
| GET | `/api/admin/accounts` | ADMIN only. |
| PATCH | `/api/accounts/*/status` | ADMIN only. |

### Tasks

1. Add a GET matcher for the administration account list and a PATCH matcher for status changes.
2. Use `hasRole("ADMIN")` for both.
3. Keep each rule above `anyRequest().denyAll()`. Spring applies the first matching rule, so the final fallback must remain last.
4. Do not replace these rules with a broad `authenticated()` rule. A signed-in customer must still be denied administrator access.

### Acceptance checks through the frontend

1. Sign in as `admin.bank@example.test`. The Account oversight screen lists Casey's and Jordan's accounts.
2. Freeze Casey's checking account. Its status becomes FROZEN.
3. Sign out and sign in as Casey. The balance and history remain visible, but the selected frozen account's money controls are disabled.
4. Sign back in as the administrator and reactivate that account. Casey can deposit or withdraw again after signing back in or refreshing.
5. Confirm a CUSTOMER never sees the account-administration controls.

For a direct backend check without adding a testing panel or using Postman, sign in as Casey and open **http://localhost:5173/api/admin/accounts** in another browser tab. The response must be **403**, with no account data. The browser may show an error page for the empty forbidden response. As ADMIN, the same URL returns the account list. The address uses the same frontend origin and session cookie.

## Definition of done

- Submit the completed **SecurityConfiguration.java** and evidence of the three tickets' frontend results.
- Only that Java source file changes. Preserve the supplied services, controllers, repositories, entities and frontend.
- Keep the provided CSRF, BCrypt and session settings.
- Keep `anyRequest().denyAll()` last.
- Be able to explain why being signed in is different from having ADMIN permission, and why an account-owner check is still needed after a role check.

## Keep this application for File I/O

Keep the same `banking_app` database. Each transaction already stores its time, type, amount, description, reference and resulting balance. A transfer has two records sharing one reference. The next exercise can use these records to generate an account statement. File generation is not part of today's work.

## References

- [Spring Security: HTTP request authorization](https://docs.spring.io/spring-security/reference/6.5/servlet/authorization/authorize-http-requests.html)
- [Spring Security: form login](https://docs.spring.io/spring-security/reference/6.5/servlet/authentication/passwords/form.html)
- [Spring Security: CSRF protection](https://docs.spring.io/spring-security/reference/6.5/servlet/exploits/csrf.html)
