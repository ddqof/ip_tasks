import base64
import mimetypes
import os.path
import socket
import ssl


class SMTPMessage:
    CRLF = '\r\n'
    TIMEOUT = 5

    def __init__(self, text_path, files, theme):
        self.login = 'a.obinovv@yandex.ru'
        self.pswd = 'gvoprlvconlbjtls'
        self.stop_symbol = '--15HmuRniMa=====OImHacfE45heReHe'
        self.header = [
            'EHLO ddqof_client',
            'AUTH LOGIN',
            base64.b64encode(self.login.encode()).decode(),
            base64.b64encode(self.pswd.encode()).decode(),
            f'MAIL FROM: {self.login}',
            None,  # addressee must be here
            'DATA']
        self.body = [
            f'From: {self.login}',
            None,  # addressee must be here
            f'Subject: {theme}',
            f'Content-Type: multipart/mixed; boundary={self.stop_symbol[2:]};',
            '']
        self._add_text(text_path)
        self._add_files(files)
        self._end_body()

    def _add_text(self, text_path):
        self.body.append(self.stop_symbol)
        self.body.append('Content-Type: text/plain; charset="utf-8"')
        self.body.append('')
        with open(text_path, 'r', encoding='utf-8') as f:
            for line in f:
                if line.endswith('\n'):
                    line = line[:-1]
                if line == '.':
                    line = '..'
                self.body.append(line)

    def _add_files(self, files):
        for filepath in files:
            filename = os.path.basename(filepath)
            with open(filepath, "rb") as f:
                self.body += [
                    self.stop_symbol,
                    f'Content-Type:{mimetypes.guess_type(filename)[0]}; name={filename}',
                    'Content-Transfer-Encoding:base64',
                    f'Content-Disposition:attachment; filename={filename}',
                    '',
                    base64.b64encode(f.read()).decode()]

    def _end_body(self):
        self.body.append(self.stop_symbol + '--')
        self.body.append('')
        self.body.append('.')

    def _set_addressee(self, addressee):
        self.header[5] = 'RCPT TO: {}'.format(addressee)
        self.body[1] = 'To: You <{}>'.format(addressee)

    def send_to(self, addressee):
        self._set_addressee(addressee)
        sock = socket.socket()
        sock.settimeout(self.TIMEOUT)
        sock.connect(('smtp.yandex.ru', 465))
        ssl_sock = ssl.wrap_socket(sock)
        print(ssl_sock.recv(1024).decode())
        try:
            for header_line in self.header:
                self._send_to(ssl_sock, header_line)
                self._handle_sock(ssl_sock)
            for body_line in self.body:
                self._send_to(ssl_sock, body_line)
            self._handle_sock(ssl_sock)
            self._send_to(ssl_sock, 'QUIT')
            self._handle_sock(ssl_sock)
        except Exception as e:
            print("Can't send message to {} because:\n{}".format(addressee, e))
        else:
            print('Message was successfully sent')
        finally:
            ssl_sock.close()
            sock.close()

    def _send_to(self, ssl_sock, msg):
        ssl_sock.send((msg + self.CRLF).encode())
        print('SENT: {}'.format(msg if len(msg) < 800 else 'big data'))

    def _handle_sock(self, ssl_sock):
        data = ssl_sock.recv(1024)
        print('RECV: {}'.format(data.decode()))
        if data.startswith(b'4') or data.startswith(b'5'):
            raise Exception("Server can't handle this message")
