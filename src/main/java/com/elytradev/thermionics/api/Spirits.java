/*
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

package com.elytradev.thermionics.api;

import com.elytradev.thermionics.data.Registry;
import com.elytradev.thermionics.item.Spirit;

import net.minecraft.init.Items;

public class Spirits {
	public static final Registry<Spirit> REGISTRY = new Registry<>(Spirit.class);
	
	static {
		REGISTRY.register(new Spirit("vodka",        Items.POTATO)  .withColor(0x77ffffff));
		REGISTRY.register(new Spirit("whiskey",      Items.WHEAT)   .withColor(0x88ef8f41));
		REGISTRY.register(new Spirit("rum",          Items.REEDS)   .withColor(0x77502f15));
		REGISTRY.register(new Spirit("moonshine",    Items.SUGAR)   .withColor(0x88ffffff)); //Processed sugar will always have higher alcohol content
		REGISTRY.register(new Spirit("hard_cider",   Items.APPLE)   .withColor(0x99f6c70b));
		REGISTRY.register(new Spirit("molasses",     Items.BEETROOT).withColor(0x994d2c1a)); //Also extremely strong for the same reason
		REGISTRY.register(new Spirit("dr_purpur",    Items.CHORUS_FRUIT_POPPED).withColor(0x44ff24fc)); //A serious drink for real discriminating persons of science
		REGISTRY.register(new Spirit("carrot_brandy",Items.CARROT)  .withColor(0x558a032f)); //This was actually a thing in WW2. Also, cabbage. Disgusting!
		REGISTRY.register(new Spirit("gin",          "berryJuniper").withColor(0x77ffffff));
		REGISTRY.register(new Spirit("tequila",      "fruitAgave")  .withColor(0x44ffde00));
	}
}
