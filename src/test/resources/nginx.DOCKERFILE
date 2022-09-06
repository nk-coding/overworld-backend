FROM nginx

RUN apt-get update && apt-get upgrade -y

COPY nginx-test.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]