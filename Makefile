.PHONY: help build up down logs clean

help:
	@echo "Available commands:"
	@echo "  make build    - Build all Docker images"
	@echo "  make up       - Start all services"
	@echo "  make down     - Stop all services"
	@echo "  make logs     - View logs"
	@echo "  make clean    - Remove containers and volumes"

build:
	docker-compose build

up:
	docker-compose up -d
	@echo "✅ Frontend: http://localhost:4200"
	@echo "✅ Backend: http://localhost:8080"
	@echo "✅ PostgreSQL: localhost:5432"

down:
	docker-compose down

logs:
	docker-compose logs -f

clean:
	docker-compose down -v
	docker system prune -f
