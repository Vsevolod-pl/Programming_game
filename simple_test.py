from Python_client.run_commands import run_match
import unittest


class TestBots(unittest.TestCase):
    def test_run():
        result_match = run_match(["bot1", "bot2"], ["Python_client.bot1", "Python_client.bot2"], [1, 2])
        self.assertEqual(result_match, {'bot1': 'win', 'bot2': 'lose'})

unittest.main()
