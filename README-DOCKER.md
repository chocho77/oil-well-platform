# 🐳 Docker инструкции

## 📦 Инсталиране на Docker

### На Arch Linux:
```bash
sudo pacman -S docker docker-compose
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
# Рестартирайте сесията
