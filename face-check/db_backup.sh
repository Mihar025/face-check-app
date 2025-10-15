#!/bin/bash
BACKUP_DIR="$HOME/db_backups"
DATE=$(date +%Y%m%d_%H%M%S)

# Создаем директорию если не существует
mkdir -p $BACKUP_DIR

# Делаем backup
PGPASSWORD="misha" pg_dump -h localhost -p 5433 -U misha face_check | gzip > $BACKUP_DIR/backup_$DATE.sql.gz

# Удаляем старые backups (старше 7 дней)
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "Backup completed: $BACKUP_DIR/backup_$DATE.sql.gz"