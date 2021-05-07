import argparse
import subprocess
from ipwhois import IPWhois, IPDefinedError


class Traceroute:

    @staticmethod
    def process_trace_line(line):
        if "* * *" not in line:
            line_without_asterisk = line
            if "*" in line:
                line_without_asterisk = line.replace("*", "")
            num_name_ip = line_without_asterisk.split()[:3]
            as_info = Traceroute.handle_as_info(num_name_ip[2][1:-1])
            name = str(num_name_ip[0]).ljust(3)
            ips = f"{num_name_ip[1]} {num_name_ip[2]}".ljust(70)
            try:
                number_country_provider = f"{as_info[0]}, {as_info[1]}, {as_info[2]}"
            except IndexError:
                number_country_provider = "-"
            return f"{name}{ips}{number_country_provider}\n"
        else:
            splits = line.split()
            line_number = str(splits[0])
            return f"{line_number.ljust(3)}* * *\n"

    @staticmethod
    def handle_as_info(ip):
        as_info = []
        try:
            ip_info = IPWhois(ip).lookup_whois()
            as_number = ip_info["asn"]
            as_country = ip_info["asn_country_code"]
            as_provider = ip_info["asn_description"]
            if as_provider == "NA":
                if ip_info["nets"]:
                    as_provider = ip_info["nets"][0]["name"]
            if as_country in as_provider:
                as_provider = as_provider.replace(f", {as_country}", "")
            as_info = [as_number, as_country, as_provider]
        except IPDefinedError:
            pass
        return as_info

    @staticmethod
    def run(target):
        counter = 0
        process = subprocess.Popen(["traceroute", target], stdout=subprocess.PIPE)
        for line in iter(lambda: process.stdout.readline(), b''):
            decoded_line = line.decode()
            if not counter:
                print(decoded_line, end="")
                counter += 1
            else:
                print(Traceroute.process_trace_line(decoded_line), end="")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Traceroute utility")
    parser.add_argument("target", type=str)
    args = parser.parse_args()
    Traceroute.run(args.target)
