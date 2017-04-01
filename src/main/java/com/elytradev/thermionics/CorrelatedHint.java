/**
 * MIT License
 *
 * Copyright (c) 2017 Isaac Ellingson (Falkreon) and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.elytradev.thermionics;

import java.util.Random;

public class CorrelatedHint {
	
	/* This constant's content is selected quotes copied in / adapted from
	 * https://en.wikiquote.org/wiki/Pinky_and_the_Brain#Are_You_Pondering_What_I.27m_Pondering.3F
	 * included here under CC-By-SA */
	private static final String[] CORRELATED_REPLIES = {
			"where are we going to find a duck and a hose at this hour?",
			"where will we find an open tattoo parlor at this time of night?",
			"if we didn't have ears, wouldn't we look like weasels?",
			"where are we going to find rubber pants our size?",
			"balancing a family and a career ... ooh, it's all too much for me.",
			"isn't Regis Philbin already married?",
			"burlap chafes me so.",
			"how are we going to find chaps our size?",
			"we'll never get a monkey to use dental floss.",
			"this time, you wear the tutu.",
			"culottes have a tendency to ride up so.",
			"if we covered the world in salad dressing wouldn't the asparagus feel left out?",
			"if they called them 'Sad Meals', kids wouldn't buy them!",
			"this time you put the trousers on the chimp.",
			"I can't memorize a whole opera in Yiddish.",
			"there's still a bug stuck in here from last time.",
			"I get all clammy inside the tent.",
			"pants with horizontal stripes make me look chubby.",
			"where do you stick the feather and call it macaroni?",
			"it's a miracle that this one grew back.",
			"first you'd have to take that whole bridge apart, wouldn't you?",
			"isn't that why they invented tube socks?",
			"what if we stick to the seat covers?",
			"I think I'd rather eat the Macarena.",
			"don't you need a swimming pool to play Marco Polo?",
			"do I really need two tongues?",
			"we're already naked.",
			"if Jimmy cracks corn, and no one cares, why does he keep doing it?",
			"don't camels spit a lot?",
			"isn't a cucumber that small called a gherkin?",
			"how can we get seven dwarves to shave their legs?",
			"calling it pu-pu platter? What were they thinking?",
			"if we give peas a chance, won't the lima beans feel left out?",
			"if we had a snowmobile, wouldn't it melt before summer?",
			"what kind of rides do they have in The End?",
			"can the Gummi Worms really live in peace with the Marshmallow Chicks?",
			"wouldn't anything lose its flavor on the bedpost overnight?",
			"three round meals a day wouldn't be as hard to swallow.",
			"if the plural of mouse is mice, wouldn't the plural of spouse be spice?",
			"why does the chicken cross the road, if not for love? I do not know.",
			"if our knees bent the other way, how would we ride a bicycle?",
			"how will we get three pink flamingos into one pair of Capri pants?",
			"Tuesday Weld isn't a complete sentence.",
			"why would anyone want to see Snow White and the Seven Samurai?",
			"I find scratching just makes it worse.",
			"shouldn't the bat boy be wearing a cape?",
			"why would anyone want a depressed tongue?",
			"why does a forklift have to be so big if all it does is lift forks?",
			"Lederhosen won't stretch that far.",
			"what would goats be doing in red leather turbans?",
			"if I put on two tutu's, would I really be wearing a four-by-four?",
			"wouldn't mustard make it sting?",
			"can you use the word 'asphalt' in polite society?",
			"then again, let's not let our enthusiasm overwhelm us!",
			"if the sun'll come out tomorrow, what's it doing right now?",
			"aren't we out of shaving cream?",
			"would the villains really have gotten away with it, if it weren't for those pesky kids and their dog?",
			"how are we gonna teach a goat to dance with flippers on?",
			"let's use safflower oil this time! It's ever so much healthier!",
			"Cream of Gorilla Soup—well, we'd have to sell it in awfully big cans, wouldn't we?",
			"do nuts go with pudding?",
			"a codpiece made from a real fish would get smelly after a while, wouldn’t it?",
			"do those roost in this neighborhood?",
			"do we have time to grease the rockets?",
			"are these really the legs of a show girl?",
			"this time I get to play the dishwasher repairman!",
			"where would you get a chicken, 20 yards of spandex and smelling salts at this hour?",
			"won't it go straight to my hips?",
			"isn't it cheating to use glue?",
			"if running shoes had little feet, wouldn't they need their own shoes?",
			"should we use dishwashing liquid or cooking oil?",
			"no, on second thought, I don't think they allow that in a book with the Comics Code.",
			"would Danish flies work just as well?",
			"dressing like twins is so tacky.",
			"practicing docking procedures with a goat at zero G's—it's never been done!",
			"shouldn't we let the silk worms finish the boxer shorts before we put them on?",
			"if a ham can operate a radio, why can't a pig set a VCR?",
			"what's the use of having a heart-shaped tattoo if it's going to be covered by hair?",
			"couldn't the constant use of a henna rinse lead to premature baldness?",
			"ruby-studded stockings would be mighty uncomfortable wouldn't they?",
			"if I have my portrait drawn, will we have time to make it to the lifeboats?",
			"how much deeper would the ocean be if there weren't sponges down there?",
			"doing a clog dance in actual clogs will give me awful blisters.",
			"nose rings are kinda passé by now.",
			"where are we going to get a trained octopus at this time of night?",
			"no more eels in jelly for me, thanks—I like my gelatin after lunch.",
			"where is a fish?",
			"my feet taste better buttered.",
			"if Pinocchio were carved out of bacon it wouldn't be the same story, would it?",
			"suppose we do the hokey pokey and turn ourselves around, is that what it's really all about?",
			"how are we going to get the bacon flavoring into the pencils?"
	};
	
	static {
		Random rnd = new Random();
		Thermionics.LOG.info("I think so, Correlated, but "+CORRELATED_REPLIES[rnd.nextInt(CORRELATED_REPLIES.length)]);
		
		try {
			Class.forName("com.elytradev.correlated.ThermionicsHint");
		} catch (Throwable t) {
			//Can't think of anything clever here
		}
	}
}
