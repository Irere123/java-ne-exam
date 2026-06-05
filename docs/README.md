# Documentation

Detailed documentation for the Utility Billing System. Start at the
[project README](../README.md) for the overview and quick start.

| Document | What's inside |
|----------|---------------|
| [Architecture](architecture.md)           | Layered design, the request pipeline, and the billing-lifecycle flow |
| [Data model](data-model.md)               | Entity-relationship diagram and the constraints that enforce the business rules |
| [Database routines](database-routines.md) | Triggers, the cursor-based stored procedure, and Flyway vendor scoping |
| [API reference](api-reference.md)         | Roles, the full endpoint surface, and request-validation rules |
| [Configuration](configuration.md)         | Prerequisites, running, environment variables, and tests |
| [Walkthrough](walkthrough.md)             | An end-to-end run through the API with Swagger or Postman |

The raw database design is also available as a DBML schema in
[`erd.dbml`](erd.dbml) (paste into <https://dbdiagram.io> to render).

## Exam-task map

This project was built against a six-task brief. Each task maps onto the
documentation as follows:

| Task | Theme | Where it's covered |
|------|-------|--------------------|
| 1 | Roles & access control          | [API reference → Roles](api-reference.md#roles) |
| 2 | Customer registration           | [Data model](data-model.md) (unique national ID / email) |
| 3 | Meter readings                  | [Architecture → Billing lifecycle](architecture.md#billing-lifecycle) |
| 4 | Versioned tariffs               | [Data model](data-model.md) (versioned tariffs) |
| 5 | Bill generation                 | [Architecture → Billing lifecycle](architecture.md#billing-lifecycle) |
| 6 | DB routines & notifications     | [Database routines](database-routines.md) |
