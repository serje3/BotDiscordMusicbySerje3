import os
import paramiko
from dotenv import load_dotenv

file_name = 'java-discord-bot-1.0.zip'


def local_deploy(hostname, username, password, local_build, app_dir):
    print("Deploy started....")
    # Создание объекта SSHClient
    client = paramiko.SSHClient()

    # Добавление хоста в список доверенных хостов (если требуется)
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    # Подключение к хосту
    client.connect(hostname, username=username, password=password)
    print("Connected to SSH!")

    scp = client.open_sftp()
    # scp.chdir(app_dir)
    print(1, scp.getcwd())
    scp.put(local_build, app_dir, callback=lambda x1, x2: print(x1, x2))
    print("Sended build zip to server")
    scp.close()

    commands = [
        "cd " + os.environ.get('APP_DIR'),
        "systemctl stop cocker.service",
        "unzip -o " + file_name,
        "systemctl start cocker.service"
    ]

    _, stdout, err = client.exec_command('; '.join(commands))
    print(stdout.read().decode())
    print(err.read().decode())
    # Закрытие соединения
    client.close()


if __name__ == "__main__":
    load_dotenv()

    hostname = os.environ.get('COCKER_HOST', None)
    password = os.environ.get('COCKER_PASSWORD', None)
    username = os.environ.get('COCKER_USERNAME', None)

    local_dir = os.path.join(os.getcwd(), 'build', 'distributions', file_name)
    app_dir = os.path.join(os.environ.get('APP_DIR'), file_name)

    if hostname is None or password is None:
        raise Exception("No hostname or password")

    local_deploy(hostname, username, password, local_dir, app_dir)
