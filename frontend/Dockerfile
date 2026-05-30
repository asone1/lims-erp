# 在台灣建議直接使用官方鏡像，CDN 支援通常很快
FROM node:20-alpine
WORKDIR /app
# 使用 root 權限安裝，確保權限沒問題
USER root
COPY package*.json ./

# 在台灣，直接使用官方 npm registry 即可，速度通常較穩定
RUN npm install --fetch-retry-maxtimeout 600000

COPY . .
EXPOSE 5173
CMD ["npm", "run", "dev"]
