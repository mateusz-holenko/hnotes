FROM python:3.10-alpine
WORKDIR /app

COPY requirements.txt /app
RUN pip3 install -r requirements.txt

COPY app/* /app

EXPOSE 5000
ENTRYPOINT ["flask", "--app", "veri", "run"]
