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

package com.elytradev.thermionics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class StringExtras {
	public static List<String> wrapForTooltips(String str) {
		return wordWrap(str, 35);
	}
	
	public static void addInformation(String key, List<String> tooltip) {
		addInformation(key, "", tooltip);
	}
	
	public static void addInformation(String key, String formatCodes, List<String> tooltip) {
		String localized = I18n.translateToLocal(key);
		List<String> wrapped = wrapForTooltips(localized);
		for(String s : wrapped) tooltip.add(formatCodes+s);
	}
	
	/**
	 * Adds text pre-split with '/', such as poems with metered lines
	 */
	public static void addSplitInformation(String key, String formatCodes, List<String> tooltip) {
		String localized = I18n.translateToLocal(key);
		for(String s : localized.split("/")) {
			tooltip.add(formatCodes + s.trim());
		}
	}
	
	private static List<String> wordWrap(String str, int width) {
		List<String> result = new ArrayList<>();
		String[] words = str.split(" ");

		String line = "";
		for (String word : words) {
			
			if (line.length() + word.length() > width) {
				//If the current line won't fit with the new word appended, emit it and try the word on its own
				if (!line.isEmpty()) {
					result.add(line.trim());
					line = "";
				}
				
				// If the current word is too long to fit even on its own, split it up
				while (word.length() > width) {
					String subword = word.substring(0, width - 1) + "-";
					result.add(subword.trim());
					word = word.substring(width - 1);
					line = ""; //unnecessary but you never know
				}
			}
			line += " "+word;
		}
		if (!line.isEmpty()) result.add(line.trim());
		
		return result;
	}
	
	public static List<String> wordWrap(String str, int width, Function<String, Integer> widthFunction) {
		List<String> result = new ArrayList<>();
		String[] words = str.split(" ");

		String line = "";
		for (String word : words) {
			if (widthFunction.apply((line+" "+word).trim()) > width) {
				//If the current line won't fit with the new word appended, emit the line so the new word sits at the start of the next line
				if (!line.isEmpty()) {
					result.add(line.trim());
					line = "";
				}
			}
			line += (" "+word).trim(); //Add it even if it won't fit by itself
		}
		if (!line.isEmpty()) result.add(line.trim());
		
		return result;
	}
}
