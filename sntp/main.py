import socket
import struct
from cfg import DELTA


class SNTPProxy:

    def __init__(self, delta):
        self._PACKET_FORMAT = "!12I"
        self._server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self._server_socket.bind(("localhost", 123))
        self._socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self._RECEIVED_TIMESTAMP_POSITIONS = (8, 9)
        self.delta = delta

    def run(self):
        while True:
            # accept message from sntp client
            message, address = self._server_socket.recvfrom(1024)
            # send client message
            self._socket.sendto(message, ("ntp3.stratum2.ru", 123))
            # recv result from server
            result, addr = self._socket.recvfrom(1024)
            # decode response to raw bytes
            decoded = struct.unpack(self._PACKET_FORMAT, result)
            recv_timestamp = (
                decoded[self._RECEIVED_TIMESTAMP_POSITIONS[0]], decoded[self._RECEIVED_TIMESTAMP_POSITIONS[1]]
            )
            result = struct.pack(
                self._PACKET_FORMAT,
                decoded[0],
                decoded[1],
                decoded[2],
                decoded[3],
                decoded[4],
                decoded[5],
                decoded[6],
                decoded[7],
                SNTPProxy._to_int(recv_timestamp[0] + self.delta * 2),
                SNTPProxy._to_frac(recv_timestamp[1]),
                decoded[10],
                decoded[11])
            self._server_socket.sendto(result, address)

    @staticmethod
    def _to_frac(timestamp, n=32):
        return int(abs(timestamp - SNTPProxy._to_int(timestamp)) * 2 ** n)

    @staticmethod
    def _to_int(timestamp):
        return int(timestamp)


if __name__ == '__main__':
    server = SNTPProxy(DELTA)
    server.run()
