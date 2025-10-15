# Сборка
FROM node:18-alpine AS build
WORKDIR /app

# Копируем package.json
COPY package*.json ./
RUN npm ci

# Копируем angular.json и модифицируем лимиты
COPY angular.json ./
RUN sed -i 's/"maximumWarning": ".*"/"maximumWarning": "5mb"/g' angular.json && \
    sed -i 's/"maximumError": ".*"/"maximumError": "10mb"/g' angular.json

# Копируем остальной код
COPY . .

# Собираем с отключенным инлайном шрифтов
RUN npm run build -- --configuration production --inline-style-language=false

# Запуск
FROM nginx:alpine
COPY --from=build /app/dist/face-check-ui /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]