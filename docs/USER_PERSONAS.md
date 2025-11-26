# User Personas (Working Draft)

Intent: Capture informal persona notes to guide UI & test scenarios. Non-executable reference.

## Persona 1: Admin Anh
- Role: System administrator
- Goals:
  - Maintain product catalog cleanliness
  - Quickly correct incorrect product entries
  - Monitor for suspicious login attempts
- Behaviors:
  - Logs in daily mornings
  - Uses search heavily
  - Prefers keyboard shortcuts (Enter to submit)
- Pain Points:
  - Slow pagination when many products
  - Re-entering common fields repeatedly
- Test Implications:
  - Need fast create/edit flows
  - Validate search responsiveness under load

## Persona 2: Seller Bình
- Role: Product contributor (future role concept)
- Goals:
  - Add multiple products rapidly
  - Ensure validation feedback is clear
- Behaviors:
  - Frequently triggers validation edges (copy-paste)
  - Might leave description short initially
- Pain Points:
  - Strict description length
  - Duplicate name rejection confusion
- Test Implications:
  - Boundary value tests for description
  - Usability of duplicate name error message

## Persona 3: Viewer Chi
- Role: Read-only viewer (future)
- Goals:
  - Browse list efficiently
  - Filter by category
- Behaviors:
  - Rarely uses create/edit
  - Scrolls through multiple pages
- Pain Points:
  - Pagination index management
  - Category filter reset not obvious
- Test Implications:
  - Add pagination navigation tests
  - Category + search compound test coverage

## Persona 4: Performance Tester Duy
- Role: Internal QA / performance engineer
- Goals:
  - Stress endpoints realistically
  - Identify slow queries early
- Behaviors:
  - Runs k6 scripts with varying VUs
  - Compares p95 and p99 weekly
- Pain Points:
  - Hard-coded scenarios adaptation
  - Lack of baseline historical record
- Test Implications:
  - Document performance thresholds
  - Maintain scenario config clarity

## Persona 5: Security Reviewer Em
- Role: Occasional security audit
- Goals:
  - Verify auth flows safe
  - Confirm no sensitive leakage
- Behaviors:
  - Tamper token signatures
  - Attempt HTML/script injection
- Pain Points:
  - Missing audit trail for changes
  - Lack of role separation currently
- Test Implications:
  - Add negative JWT tests
  - Injection rejection checks

## Persona 6: Accessibility User Phúc
- Role: User with keyboard-only interaction
- Goals:
  - Navigate forms without mouse
  - Understand error context
- Behaviors:
  - Uses Tab / Shift+Tab strictly
  - Relies on visible focus states
- Pain Points:
  - Placeholder-only fields (no label)
  - Modal focus trap missing
- Test Implications:
  - Add keyboard navigation test cases
  - Validate visible focus outline

## Persona 7: Data Analyst Gia (Future)
- Role: Consumes product dataset exports
- Goals:
  - Reliable product data formats
  - Detect anomalies (duplicate, missing category)
- Behaviors:
  - Exports nightly snapshots (not implemented yet)
- Pain Points:
  - No built-in export currently
- Test Implications:
  - Potential future tests around export stability

## Aggregated Testing Hooks Inspired by Personas
| Concern | Derived Test Idea | Current Status |
|---------|-------------------|----------------|
| Fast admin edits | Edit latency under 1000ms (perf) | Partially measured |
| Duplicate clarity | Error message assert exact string | Covered basic |
| Pagination reliability | Navigate page end boundary | Missing |
| Keyboard accessibility | Enter key triggers login | Missing |
| Token tamper rejection | Alter header/payload signature | Missing |
| Injection safety | Description with <script> stripped or rejected | Missing |
| Focus management | Modal initial focus on first field | Partial |

## Notes
Personas are lightweight; not full UX research artifacts.
They justify certain pending tests without inflating feature scope prematurely.
Extend only when new behavior emerges.

END OF FILE
