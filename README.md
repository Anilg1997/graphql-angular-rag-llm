# GraphQL Angular - RAG LLM Agentic AI

A full-stack application combining **GraphQL CRUD** with **RAG (Retrieval-Augmented Generation)**, **LLM integration**, **AI Agents**, and **MCP (Model Context Protocol)** — all powered by **Ollama** (local, free LLMs).

## Architecture

```
Angular 19 Frontend → Spring Boot Backend → LangChain4j → Ollama (llama3.2 + nomic-embed-text)
                                       → MongoDB
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Angular 19 (standalone components, HttpClient) |
| Backend | Spring Boot 3.5.15, Java 24 |
| API | GraphQL + REST |
| Database | MongoDB |
| AI Framework | LangChain4j (Java port of LangChain) |
| LLM | Ollama - llama3.2 (chat), nomic-embed-text (embeddings) |
| Vector Store | In-memory with cosine similarity search |
| Protocol | MCP (Model Context Protocol) JSON-RPC |

## Features

- **Student CRUD** — Create, Read, Update, Delete via GraphQL
- **AI Chat** — Conversational AI via Ollama (llama3.2)
- **RAG** — Ingest documents → embed → semantic search → answer with context
- **AI Agent** — Intent classification + tool execution (CRUD operations)
- **MCP** — Model Context Protocol for dynamic tool discovery and invocation
- **Document Store** — MongoDB + Vector embeddings

## Prerequisites

- Java 21+
- MongoDB (running on localhost:27017)
- Ollama with models: `llama3.2`, `nomic-embed-text`

```bash
ollama pull llama3.2
ollama pull nomic-embed-text
```

## Quick Start

```bash
# 1. Start MongoDB
.\start-mongodb-simple.bat

# 2. Start Ollama (ensure models are available)
ollama serve

# 3. Start Backend
.\mvnw.cmd spring-boot:run

# 4. Start Frontend (separate terminal)
cd frontend
npx ng serve --open
```

Or use the all-in-one script: `.\run-all.bat`

## URLs

| Service | URL |
|---------|-----|
| GraphQL IDE (GraphiQL) | http://localhost:8080/graphiql |
| Angular UI | http://localhost:4200 |
| REST AI Chat | POST http://localhost:8080/api/ai/chat |
| REST RAG Query | POST http://localhost:8080/api/ai/rag |
| REST AI Agent | POST http://localhost:8080/api/ai/agent |
| MCP JSON-RPC | POST http://localhost:8080/mcp/rpc |

## Sample GraphQL Queries

```graphql
# Student CRUD
{ getStudents { id name email course } }

# AI Chat
{ aiChat(message: "What is RAG?") { response } }

# RAG (seed documents first)
mutation { ingestDocuments }
{ aiRag(question: "What is Java?") { response sources } }

# AI Agent
{ aiAgent(task: "list all students") { response } }
```

## Project Structure

```
src/main/java/com/example/graphql_angular/
├── ai/
│   ├── config/         # LangChain4j, Ollama, GraphQL scalar config
│   ├── controller/     # REST + GraphQL resolvers
│   ├── model/          # AI-related POJOs
│   ├── repository/     # MongoDB repositories
│   ├── service/        # LLM, Embeddings, VectorStore, RAG, Agent, MCP
│   └── tool/           # Student CRUD tools for Agent
├── controller/         # Student CRUD GraphQL resolver
├── model/              # Student entity
├── repository/         # Student repository
└── service/            # Student service
```

## Test Results (all verified)

| Endpoint | Status |
|----------|--------|
| GraphQL CRUD | ✅ Working |
| AI Chat (REST + GraphQL) | ✅ Working |
| RAG Query | ✅ Working (context + sources) |
| AI Agent | ✅ Working (CRUD + general chat) |
| MCP Capabilities | ✅ Working |
| MCP tools/call | ✅ Working |
