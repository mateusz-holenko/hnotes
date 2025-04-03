FROM python:3.10-alpine
WORKDIR /app

COPY requirements.txt /app
COPY ./veri.py /app/veri.py

RUN pip3 install -r requirements.txt

EXPOSE 5000
ENTRYPOINT ["flask", "--app", "veri", "run", "--host=0.0.0.0"]
