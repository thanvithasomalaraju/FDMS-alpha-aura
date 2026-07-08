# FDMS Deployment Guide

## Prerequisites
- Linux server (Ubuntu 20.04+) or any cloud server
- Docker (optional but recommended)
- MySQL database
- Domain name (optional)

## Deployment Options

## Option 1: Direct Deployment on Linux

### Step 1: Server Setup
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Install MySQL
sudo apt install mysql-server -y

# Install Maven
sudo apt install maven -y
```

### Step 2: Clone & Build
```bash
# Clone repository
git clone https://github.com/thanvithasomalaraju/FDMS-alpha-aura.git
cd FDMS-alpha-aura

# Create database
mysql -u root -p < database/schema.sql

# Configure application.properties
sudo nano src/main/resources/application.properties

# Build application
mvn clean install -DskipTests
```

### Step 3: Run Backend
```bash
# Run with nohup to keep running after terminal close
nohup java -jar target/fdms-backend-1.0.0.jar > backend.log &

# Check if running
lsof -i :8080
```

### Step 4: Frontend Deployment
```bash
# Install Nginx
sudo apt install nginx -y

# Copy frontend files
sudo cp -r frontend/* /var/www/html/

# Configure Nginx
sudo nano /etc/nginx/sites-available/default

# Add proxy to backend
locations /api/ {
    proxy_pass http://localhost:8080/api/;
}

# Restart Nginx
sudo systemctl restart nginx
```

## Option 2: Docker Deployment

### Create Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/fdms-backend-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
```

### Create Docker Compose
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: mad_food_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database:/docker-entrypoint-initdb.d
  
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/mad_food_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    depends_on:
      - mysql
  
  nginx:
    image: nginx:latest
    ports:
      - "80:80"
    volumes:
      - ./frontend:/usr/share/nginx/html
    depends_on:
      - backend

volumes:
  mysql_data:
```

### Deploy with Docker
```bash
# Build and start
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

## Option 3: Cloud Deployment

### AWS EC2
1. Launch EC2 instance (Ubuntu 20.04)
2. Security group: Open ports 22, 80, 443, 8080, 3306
3. Follow "Direct Deployment" steps above
4. Use Elastic IP for static IP

### Heroku
1. Create `Procfile`:
   ```
   web: java -Dserver.port=$PORT $JAVA_OPTS -jar target/fdms-backend-1.0.0.jar
   ```
2. Deploy:
   ```bash
   heroku login
   heroku create your-app-name
   git push heroku main
   ```

### DigitalOcean
1. Create Droplet (Ubuntu 20.04)
2. SSH into droplet
3. Follow "Direct Deployment" steps
4. Use Floating IP for static IP

## Production Configuration

### application.properties
```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Database (Production)
spring.datasource.url=jdbc:mysql://db-host:3306/mad_food_db
spring.datasource.username=prod_user
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=604800000

# Logging
logging.level.root=WARN
logging.level.com.madfood=INFO

# CORS
server.cors.allowed-origins=${FRONTEND_URL}
```

### Environment Variables
```bash
# Set in production
export DB_PASSWORD="secure_password"
export JWT_SECRET="production_secret_key"
export FRONTEND_URL="https://yourdomain.com"
```

## SSL/HTTPS Setup

### Using Let's Encrypt with Nginx
```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Get certificate
sudo certbot certonly --nginx -d yourdomain.com

# Configure Nginx for HTTPS
sudo nano /etc/nginx/sites-available/default

# Restart Nginx
sudo systemctl restart nginx
```

## Monitoring & Logs

### Backend Logs
```bash
# View real-time
tail -f backend.log

# Search for errors
grep ERROR backend.log

# Last 100 lines
tail -100 backend.log
```

### Database Monitoring
```bash
# Check size
mysql -u root -p -e "SELECT SUM(data_length + index_length) FROM information_schema.TABLES WHERE table_schema = 'mad_food_db';" 

# Backup database
mysqldump -u root -p mad_food_db > backup.sql
```

## Maintenance

### Regular Backups
```bash
# Daily backup
0 2 * * * mysqldump -u root -p${DB_PASSWORD} mad_food_db > /backups/db_$(date +\%Y\%m\%d).sql
```

### Update Security
```bash
# Update Java
sudo apt update && sudo apt install openjdk-17-jdk -y

# Update dependencies
mvn dependency:tree | grep "CVE"
```

### Restart Services
```bash
# Restart backend
sudo systemctl restart madfood-backend

# Restart MySQL
sudo systemctl restart mysql

# Restart Nginx
sudo systemctl restart nginx
```

## Troubleshooting

### Backend Won't Start
```bash
# Check logs
journalctl -u madfood-backend -n 50

# Check port
sudo lsof -i :8080

# Check Java
java -version
```

### Database Connection Issues
```bash
# Check MySQL status
sudo systemctl status mysql

# Check connection
mysql -h localhost -u root -p -e "SELECT 1;"
```

### CORS Errors
- Update FRONTEND_URL in environment variables
- Restart backend
- Clear browser cache

## Performance Optimization

### Database Indexing
```sql
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_restaurant_id ON foods(restaurant_id);
CREATE INDEX idx_customer_id ON orders(customer_id);
```

### Caching
```properties
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

## Health Check

```bash
# Backend health
curl http://localhost:8080/api/health

# Database health
mysql -u root -p -e "SELECT 1;"

# Server health
uptime
df -h
free -h
```
