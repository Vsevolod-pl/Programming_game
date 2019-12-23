from run_all import run_all
import unittest


class TestBots(unittest.TestCase):
    def test_run(self):
        result_match = run_all(["bot1", "bot2"], ["Python_client.bot1", "Python_client.bot2"], [1, 2])
        self.assertEqual(result_match, {'bot1': 'win', 'bot2': 'lose'})

unittest.main()
