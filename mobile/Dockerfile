# ============================================
# Mobile App — Development Only
# ============================================
FROM node:20-alpine

WORKDIR /app

# Copy dependency manifests first (layer caching)
COPY package.json package-lock.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Expose Ionic dev server port
EXPOSE 8100

# Start Ionic dev server, accessible from outside the container
CMD ["npm", "run", "start", "--", "--host=0.0.0.0", "--port=8100"]
