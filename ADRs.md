# Architecture Decision Records

## 1. TCLA Model as Standalone App

### Date

18 Sept 2023

### 1.1 Context & Problem

Team is small and it is important to focus on product delivery instead of managing a lot of technical details.

Time constraints: MVP should be delivered by end of November 2023

TCLA Model execution can take up to 2 minutes to complete.

TCLA Model reads a file from the local filesystem and writes the output to the local filesystem.

Regarding running the TCLA Model, we have to choose among the following options:
- TCLA Model is embedded inside the TCLA Web Service
- TCLA Model is an standalone app

**Option A**. TCLA Model is embedded inside the TCLA Web Service:
- Pro: no need of additional infrastructure
- Pro: no need of adding an HTTP API
- Con: Since it starts a new OS process, we have to track the OS process execution state.
- Con: We have to manage OS process failures: be aware of if it hangs, kill and restart.

**Option B**. TCLA Model as Standalone App:
- Con: additional infrastructure needed
- Con: new HTTP API needed
- Pro: No need to track process state at OS level since it is automatically managed by a framework.
- Pro: No need to manage OS process failures.

### 1.2 Decision

**TCLA Model will be a standalone app** because we will spend less time to maintain TCLA model in the medium to long term.

## 2. Asynchronous communication between TCLA Web Service and TCLA model

### Date

18 Sept 2023

### 1.1 Context & Problem

Team is small and it is important to focus on product delivery instead of managing a lot of technical details.

Time constraints: MVP should be delivered by end of November 2023

TCLA Model execution can take up to 2 minutes to complete.

TCLA Model reads a file from the local filesystem and writes the output to the local filesystem.

Regarding the communication between TCLA Web Service and TCLA Model, we have to choose among the following options:
- Synchronous
- Asynchronous

**Option A**. Synchronous:
- Pro: Event-based communication code not needed
- Pro: Event-based infrastructure not needed
- Con: Tight coupling between TCLA Web Service and TCLA Model.
- Con: Retry policy implementation needed.

**Option B**. Asynchronous:
- Con: Async communication code needed
- Con: Event-based infrastructure needed if we use Pub/Sub pattern (but it can be avoided if we use other patterns)
- Pro: Loose coupling between TCLA Web Service and TCLA Model.
- Pro: Retry policy implementation not needed.

### 1.2 Decision

**Communication between TCLA Web Service and TCLA model will be Asynchronous** because we value more to have a more resilient system.

