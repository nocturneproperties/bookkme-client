[← Operations](../README.md)

# Create client

`CreateClient(client)` → `POST /api/business/{businessId}/clients`
· called from `CreateClientViewModel`

Name, last name, phone, email and description are trimmed first (`Client.trimmed()`). The server does all validation (contact info, name, phone format, email already used by another client of the business). Several clients may share
a phone; only a taken email is rejected, with `BUSINESS_CLIENT_EXISTS` (200004), which maps to
`CreateClient.Error.EmailTaken`. `CreateClientViewModel` shows it inline on the email field and keeps
the form open; editing the email clears it. A business with its limit of clients (20,000 unless the backend
raises it) answers `BUSINESS_CLIENTS_LIMIT_REACHED` (200046), which maps to `CreateClient.Error.ClientsLimitReached`
and is shown as a message. Every other failure is rethrown unchanged.

```mermaid
flowchart TD
    Start([invoke client]) --> Trim[client = client.trimmed<br/>name, lastName, phone, email, description]
    Trim --> Net[ClientsDataSource.createClient<br/>POST /api/business/businessId/clients]
    Net -- 2xx created --> Save[(saveClientsInDb listOf created)]
    Save --> R([return created Client])
    Net -- 422 BUSINESS_CLIENT_EXISTS 200004 --> Taken([throw CreateClient.Error.EmailTaken])
    Net -- 422 BUSINESS_CLIENTS_LIMIT_REACHED 200046 --> Limit([throw CreateClient.Error.ClientsLimitReached])
    Net -- other error --> EX([rethrow])
```
