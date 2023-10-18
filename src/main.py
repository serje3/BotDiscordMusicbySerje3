import asyncio

import discord
from discord.ext import commands

from src.cogs.music import Music


async def setup(bot):
    await bot.add_cog(Music(bot))


def run_bot():
    intents = discord.Intents.all()
    bot = commands.Bot(command_prefix=commands.when_mentioned_or('!'), intents=intents)
    asyncio.run(setup(bot))
    bot.run("NzE2MDQxNjY5Mzg0MDc3MzQz.G5cjwK.72rqAIFVQCSJOfwwI_bliWzKj_7Pg2ch8w4xgw")


if __name__ == '__main__':
    run_bot()
