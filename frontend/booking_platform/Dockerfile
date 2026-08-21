# ---------------------------------------------------------
# Build Stage
# ---------------------------------------------------------
FROM node:21-alpine AS build
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the entire project
COPY . .

# Build Angular app
RUN npm run build

# ---------------------------------------------------------
# Production Stage (NGINX)
# ---------------------------------------------------------
FROM nginx:stable-alpine AS production

# Copy Angular build output
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port 80 (NGINX default)
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
