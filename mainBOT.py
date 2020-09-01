import asyncio
import os

import discord
import youtube_dl
import random

from dataQueries import ManageDB
from discord.ext import commands

# Suppress noise about console usage from errors
youtube_dl.utils.bug_reports_message = lambda: ''

ytdl_format_options = {
    'format': 'bestaudio/best',
    'outtmpl': '%(extractor)s-%(id)s-%(title)s.%(ext)s',
    'restrictfilenames': True,
    'noplaylist': True,
    'nocheckcertificate': True,
    'ignoreerrors': False,
    'logtostderr': False,
    'quiet': True,
    'no_warnings': True,
    'default_search': 'auto',
    'source_address': '0.0.0.0'  # bind to ipv4 since ipv6 addresses cause issues sometimes
}

ffmpeg_options = {
    'options': '-vn'
}

ytdl = youtube_dl.YoutubeDL(ytdl_format_options)

bot = commands.Bot(command_prefix=commands.when_mentioned_or('!'))


class YTDLSource(discord.PCMVolumeTransformer):
    def __init__(self, source, *, data, volume=0.5):
        super().__init__(source, volume)

        self.data = data

        self.title = data.get('title')
        self.url = data.get('url')

    @classmethod
    async def from_url(cls, url, *, loop=None, stream=False):
        loop = loop or asyncio.get_event_loop()
        data = await loop.run_in_executor(None, lambda: ytdl.extract_info(url, download=not stream))

        if 'entries' in data:
            # take first item from a playlist
            data = data['entries'][0]

        filename = data['url'] if stream else ytdl.prepare_filename(data)
        return cls(discord.FFmpegPCMAudio(filename, **ffmpeg_options), data=data)


on_member_update_enabled = False


@bot.event
async def on_ready():
    print('Logged in as')
    print(bot.user.name)

    print('------')


class Music(commands.Cog):
    def __init__(self, bot):
        self.bot = bot

    def afterSong(self, ctx):
        Bot = bot.get_guild(ctx.message.guild.id).get_member(716041669384077343)
        color = discord.Colour(0x3a989b)
        coro = Bot.roles[1].edit(name="Музыка", colour=color, hoist=False)
        asyncio.run_coroutine_threadsafe(coro, self.bot.loop)

    @commands.command()
    async def join(self, ctx, *, channel: discord.VoiceChannel):
        """Joins a voice channel"""

        if ctx.voice_client is not None:
            return await ctx.voice_client.move_to(channel)

        await channel.connect()

    @commands.command()
    async def yt(self, ctx, *, url):
        """Plays from a url (almost anything youtube_dl supports)"""

        async with ctx.typing():
            player = await YTDLSource.from_url(url, loop=self.bot.loop)
            ctx.voice_client.play(player, after=self.afterSong(ctx))

        await ctx.send('Сейчас играет: {}'.format(player.title))
        player_title = str(player.title)
        Bot = bot.get_guild(ctx.message.guild.id).get_member(716041669384077343)
        # rgb(54, 250, 255);
        # -rgb(58, 152, 155);
        if Bot.roles[1].name != str(player_title):
            print(Bot.roles[1])
            color = discord.Colour(0x36faff)
            await Bot.roles[1].edit(name=str(player_title), color=color, hoist=True)
        if (player_title == "Голосование битый час"):
            await ctx.send("https://tenor.com/view/flick-esfand-esfandtv-ricardo-milos-ricardo-flick-gif-13730968")
            await asyncio.sleep(4)
            await ctx.send(
                "https://tenor.com/view/ponasenkov-%d0%bf%d0%be%d0%bd%d0%b0%d1%81%d0%b5%d0%bd%d0%ba%d0%be%d0%b2-%d1%82%d0%b0%d0%bd%d0%b5%d1%86-%d0%bc%d0%b0%d1%8d%d1%81%d1%82%d1%80%d0%be-dance-gif-15552318")

            await asyncio.sleep(4.1)
            await ctx.send("https://tenor.com/view/gachi-gachimuchi-karate-gif-13510787")
            await asyncio.sleep(3.9)
            await ctx.send("https://tenor.com/view/senjougahara-monogatari-series-anime-gif-gif-15534766")
            await asyncio.sleep(4)
            await ctx.send("https://tenor.com/view/gabkini-twitchitalia-gif-14901405")
            await asyncio.sleep(4)
            await ctx.send("https://tenor.com/view/im-gaxi-gaxi-the-real-im-gaxi-ethan-tremblay-ethan-gif-14071428")
            await asyncio.sleep(4)
            await ctx.send("https://tenor.com/view/pepega-pls-xqc-dance-pepega-dance-pepe-pls-pepega-gif-16147647")
            await asyncio.sleep(2.9)
            await ctx.send("https://tenor.com/view/gachi-gachi-hyper-gif-15959866")
            await asyncio.sleep(10.6)
            await ctx.send("https://tenor.com/view/gachiw-gachi-gachibass-billy-herrington-gif-16079041")
            await asyncio.sleep(4)
            await ctx.send(
                "https://tenor.com/view/%d0%bf%d0%be%d0%bd%d0%b0%d1%81%d0%b5%d0%bd%d0%ba%d0%be%d0%b2-%d1%82%d0%b0%d0%bd%d0%b5%d1%86-%d0%b3%d0%b5%d0%bd%d0%b8%d0%b9-%d0%bc%d0%b0%d1%8d%d1%81%d1%82%d1%80%d0%be-punch-gif-15552297")
            await asyncio.sleep(8)
            await ctx.send("https://tenor.com/bdp13.gif")
            await asyncio.sleep(8)
            await ctx.send("https://tenor.com/view/ricardo-flick-dance-weekend-vibe-gif-13753340")

            await asyncio.sleep(8)
            await ctx.send("https://tenor.com/view/gachi-gachimuchi-flex-lets-do-this-gif-16644616")
            await asyncio.sleep(8)
            await ctx.send("https://tenor.com/view/knut-hips-shake-gachi-sexy-gif-15908380")
            await asyncio.sleep(8)
            await ctx.send("https://media.discordapp.net/attachments/675762801049862206/715649594641350776/5yaH2Fl.gif")
            await asyncio.sleep(8)
            await ctx.send("https://tenor.com/view/gachi-ricardo-milos-dance-gif-13294294")

    @commands.command()
    async def volume(self, ctx, volume: int):
        """Changes the player's volume"""

        if ctx.voice_client is None:
            return await ctx.send("Не присоединено к голосовому чату")

        # if volume > 100:
        #     volume = 100
        # elif volume < 0:
        #     volume = 0

        ctx.voice_client.source.volume = volume / 100

        await ctx.send("Громкость: {}%".format(volume))

    @commands.command()
    async def stop(self, ctx):
        """Stops and disconnects the bot from voice"""

        await ctx.voice_client.disconnect()
        player_title = "Музыка"
        Bot = bot.get_guild(ctx.message.guild.id).get_member(716041669384077343)
        if Bot.roles[1].name != str(player_title):
            color = discord.Colour(0x3a989b)
            await Bot.roles[1].edit(name=str(player_title), colour=color, hoist=False)

    @commands.command()
    async def golosovanie(self, ctx):
        await self.yt(ctx, url="https://www.youtube.com/watch?v=dhhTNEJbEQ4")

    @commands.command()
    async def kpop(self, ctx):
        await self.yt(ctx, url="https://www.youtube.com/watch?v=tvhVVbFyt4E")
        await ctx.send("https://media.tenor.com/images/063676b4372b2a7ec0833ddf0ffdd1e6/tenor.gif")

    @commands.command()
    async def zawarudo(self, ctx):
        await self.yt(ctx, url="https://www.youtube.com/watch?v=8OhMBtyElhQ")

    @commands.command()
    async def relax(self, ctx, index=0):
        await self.yt(ctx, url="https://www.youtube.com/watch?v=NLB4Fcxh1iU")

    @golosovanie.before_invoke
    @yt.before_invoke
    async def ensure_voice(self, ctx):
        if ctx.voice_client is None:
            if ctx.author.voice:
                await ctx.author.voice.channel.connect()
            else:
                await ctx.send("Не присоединено к голосовому чату.")
                raise commands.CommandError("Пользователь не присоединён к голосовому чату.")
        elif ctx.voice_client.is_playing():
            ctx.voice_client.stop()
            player_title = "Музыка"
            Bot = bot.get_guild(ctx.message.guild.id).get_member(716041669384077343)
            if Bot.roles[1].name != str(player_title):
                color = discord.Colour(0x3a989b)
                await Bot.roles[1].edit(name=str(player_title), colour=color, hoist=False)


class SongList(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.database = ManageDB()

    @commands.command()
    async def showList(self, ctx):
        List = self.database.select(ctx.guild.id)
        if List != []:
            await ctx.send('id\tНазвание')
            msg = []
            for song in List:
                msg.append(str(song[0]) + '   ' + song[1] + '\n')

            lenList = [len(value) for value in ['hello', ' world']]
            if sum(lenList) <= 2000:
                await ctx.send("".join(msg))
            else:

                a = [value + sum(lenList[:i]) for value, i in [[lenList[i], i] for i in range(len(lenList))] if i != 0]
                indexes = [0]
                for i in range(len(a)):
                    if a[i] - indexes[::-1][0] >= 2000:
                        indexes.append(i)
                for i in indexes:
                    if i == 0:
                        continue
                    await ctx.send("".join(msg[:i]))
                    del msg[:i]
        else:
            await ctx.send('Не найдено')

    @commands.command()
    async def playlist(self, ctx, _id=1):
        Bot = bot.get_guild(ctx.message.guild.id).get_member(716041669384077343)
        list = self.database.select(ctx.guild.id)
        # await self.yt(ctx,url=list[id][1])
        await ctx.send("Играет плейлист")
        for id, song, guild in list:
            print(song)
            if id < _id:
                continue

            async with ctx.typing():
                player = await YTDLSource.from_url(song, loop=self.bot.loop)
                ctx.voice_client.play(player, after=lambda e: print('Player error: %s' % e) if e else None)

            player_title = player.title
            if Bot.roles[1].name != f"Плейлист ID {id}":
                print(Bot.roles[1])
                color = discord.Colour(0x36faff)
                await Bot.roles[1].edit(name=f"Плейлист ID {id}", color=color, hoist=True)
            await ctx.send(f"Плейлист ID {id} - {player_title}")
            await asyncio.sleep(int(player.data['duration']))
        await Bot.roles[1].edit(name="Музыка", colour=discord.Colour(0x3a989b), hoist=False)

    @commands.command()
    async def add(self, ctx, *songs):
        guild = ctx.guild.id
        songsList = []
        print(songs)
        text = ""
        for song in songs:
            if song[::-1][0] == ',':
                text += song
                songsList.append(text.replace(',', ''))
                text = ""
            else:
                text += song + " "
        if text != "":
            songsList.append(text.replace(',', ''))
        list = [(song, guild) for song in songsList]
        self.database.insert(list)
        await ctx.send(f"Сохранены: {', '.join(songsList)}"[:2000])

    @commands.command()
    async def drop(self, ctx, *_id):
        if _id[0] == 'all':
            list = self.database.select(ctx.guild.id)
            index = list[::-1][0][0]
            toDelete = [i for i in range(1, index + 1)]
            self.database.drop(toDelete, ctx.guild.id)
            await ctx.send("Удалено всё")
        else:
            self.database.drop(_id, ctx.guild.id)
            await ctx.send("Удалено")

    @playlist.before_invoke
    async def ensure_voice(self, ctx):
        if ctx.voice_client is None:
            if ctx.author.voice:
                await ctx.author.voice.channel.connect()
            else:
                await ctx.send("Не присоединено к голосовому чату.")
                raise commands.CommandError("Пользователь не присоединён к голосовому чату.")
        elif ctx.voice_client.is_playing():
            ctx.voice_client.stop()


class NotMentionedCommands(commands.Cog):

    @commands.command()
    async def Sobaka(self, ctx):
        await ctx.send("я @ ты @" * 250,
                       tts=True)

    @commands.command()
    async def kpekep(self, ctx, text):
        await ctx.send(text[::2])

    @commands.command()
    async def yey(self, ctx):
        await ctx.send("ye" * 250, tts=True)

    @commands.command()
    async def bruh(self, ctx):
        await ctx.send("bruh", tts=True)

    @commands.command()
    async def Lord_Of_Pidarases(self, ctx):
        guild = bot.get_guild(ctx.message.guild.id)
        all_users = []
        for x in guild.members:
            if str(x.id) != "716041669384077343":
                all_users.append(x)
        pidor = all_users[random.randint(0, len(all_users) - 1)]
        if len(pidor.roles) == 1:
            await ctx.send("Главный пидорас дня: " + str(pidor.mention),
                           tts=True)
        else:
            await ctx.send("Главный пидорас дня: " + str(pidor.name) + " " + pidor.top_role.mention,
                           tts=True)

    @commands.command()
    async def Nigguh(self, ctx):
        await ctx.send("Niggaaaah", tts=True)

    @commands.command()
    async def info(self, ctx):
        await ctx.send("Список команд:")
        await ctx.send("Музыка:" + "\n" +
                       "\t!join + <имя голосового канала>" + "\n"
                                                             "\t!yt + <ссылка на видео Youtube>" + "\n" +
                       "\t!stop остановить бота и выкинуть его из voice чата" + "\n" +
                       "\t!volume + <громкость от 1 до 100>\n"
                       "\t!golosovanie\n" +
                       "\t!relax\n" +
                       "\t!zawarudo\n")
        await ctx.send("Другие извращения:" + "\n" +
                       "\t!yey" + "\n" +
                       "\t!bruh" + "\n" +
                       "\t!Sobaka" + "\n" +
                       "\t!Lord_Of_Pidarases" + "\n" +
                       "\t!Nigguh\n" +
                       "\t!gandon\n" +
                       "\t!urperdakisunderattack\n")
        await ctx.send("Настройки бота:\n" +
                       "\t!on_mUpdateset <значения: + - включить, - выключить> выключает все отслеживания")
        await ctx.send("Плейлисты:\n" +
                       "\t!add <название> - добавить песню или несколько песен разделяющиеся запятой и пробелом ', '\n" +
                       "\t!playlist <id(по умолчанию самый первый)>\n" +
                       "\t!showList - отобразить плейлист\n" +
                       "\t!drop <'all' - чтобы удалить всё из плейлиста ; либо один или несколько id через пробел, которые указаны в !showList"
                       )

    @commands.command()
    async def gandon(self, ctx):
        await ctx.send("пососеш)" + ctx.author.mention)

    @commands.command()
    async def urperdakisunderattack(self, ctx):
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)
        await ctx.send(bot.get_guild(675762800588750986).get_role(713763255646027897).mention)


@bot.command()
async def on_mUpdateset(ctx, ans):
    global on_member_update_enabled
    if (ans == "+"):
        on_member_update_enabled = True
    elif (ans == "-"):
        on_member_update_enabled = False


@bot.event
async def on_member_join(member):
    guild = member.guild
    if guild.system_channel is not None:
        to_send = f'Сочувстую {member.mention} ты в {guild.name}!, алоооо, @everyone'
        await guild.system_channel.send(to_send, tts=True)


@bot.event
async def on_member_update(before, after):
    global on_member_update_enabled
    if (on_member_update_enabled == False):
        return
    elif (on_member_update_enabled == True):

        if (after.id == 553191333498454029) and (after.nick != "Ivan 20 cm"):
            member = after
            await member.edit(nick="Ivan 20 cm")
        if (after.status == discord.Status.offline):
            await bot.get_guild(after.guild.id).system_channel.send("Bruh " + str(after) + " не в сети")
        elif (before.status == discord.Status.offline) and (after.status == discord.Status.online):
            await bot.get_guild(after.guild.id).system_channel.send("Bruh " + str(after) + " в сети")
        if (len(before.activities) == 1) and (len(after.activities) > 1):
            if after.activities[1].type is discord.ActivityType.playing:

                if ("StartupWindow" == str(after.activities[1].name)):
                    return
                await bot.get_guild(before.guild.id).system_channel.send(str(after)[:len(str(after)) - 5] + "(" + str(
                    after.activities[0].name) + ")" + " сейчас играет в " + str(after.activities[1].name), tts=False)
        elif (len(before.activities) > 1) and (len(after.activities) > 1):
            if ((before.activities[1].name != after.activities[1].name) and (
                    after.activities[1].type is discord.ActivityType.playing)):
                if ("StartupWindow" == str(after.activities[1].name)):
                    return
                await bot.get_guild(before.guild.id).system_channel.send(str(after)[:len(str(after)) - 5] + "(" + str(
                    after.activities[0].name) + ")" + " сейчас играет в " + str(after.activities[1].name), tts=False)


@bot.event
async def on_guild_join(guild):
    await guild.system_channel.send("https://tenor.com/bhyep.gif")


bot.add_cog(Music(bot))

bot.add_cog(NotMentionedCommands(bot))

bot.add_cog(SongList(bot))

token = os.environ.get('TOKEN')

bot.run(token)
