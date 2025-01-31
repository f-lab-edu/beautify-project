#!/bin/bash
set -e

echo "Starting Kafka broker..."
/etc/confluent/docker/run &

echo "Waiting for Zookeeper to start..."
# Zookeeper가 시작될 때까지 대기
while ! nc -z zookeeper 2181; do
  sleep 1
done
echo "Zookeeper is up!"

echo "Waiting for Kafka to start..."
# Kafka가 시작될 때까지 대기
while ! nc -z kafka 29092; do
  sleep 1
done
echo "Kafka is up and running!"

# Kafka 토픽 생성
echo "Creating Kafka topic 'shop-like'..."
kafka-topics --create \
  --bootstrap-server kafka:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic shop-like || echo "Topic 'shop-like' already exists."

echo "Kafka initialization completed!"
wait
