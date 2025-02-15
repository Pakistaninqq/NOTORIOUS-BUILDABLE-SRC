package me.gavin.notorious.util.font;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CFontRenderer extends CFont {

    protected CFont.CharData[] boldChars = new CFont.CharData[256];
    protected CFont.CharData[] italicChars = new CFont.CharData[256];
    protected CFont.CharData[] boldItalicChars = new CFont.CharData[256];
    private final int[] colorCode = new int[32];
    private final String colorcodeIdentifiers = "0123456789abcdefklmnor";
    String fontName = "Arial";
    int fontSize = 18;
    protected DynamicTexture texBold;
    protected DynamicTexture texItalic;
    protected DynamicTexture texItalicBold;

    public CFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        this.setupMinecraftColorcodes();
        this.setupBoldItalicIDs();
    }

    public String getFontName() {
        return this.fontName;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public void setFontName(String newName) {
        this.fontName = newName;
    }

    public void setFontSize(int newSize) {
        this.fontSize = newSize;
    }

    public float drawStringWithShadow(String text, double x, double y, Color color) {
        float shadowWidth = this.drawString(text, x + 1.0D, y + 1.0D, color, true);

        return Math.max(shadowWidth, this.drawString(text, x, y, color, false));
    }

    public float drawString(String text, float x, float y, Color color) {
        return this.drawString(text, (double) x, (double) y, color, false);
    }

    public float drawCenteredStringWithShadow(String text, float x, float y, Color color) {
        return this.drawStringWithShadow(text, (double) (x - (float) (this.getStringWidth(text) / 2)), (double) y, color);
    }

    public float drawCenteredString(String text, float x, float y, Color color) {
        return this.drawString(text, x - (float) (this.getStringWidth(text) / 2), y, color);
    }

    public float drawString(String text, double x, double y, Color gsColor, boolean shadow) {
        --x;
        y -= 2.0D;
        Color color = gsColor;

        if (text == null) {
            return 0.0F;
        } else {
            if (gsColor.getRed() == 255 && gsColor.getGreen() == 255 && gsColor.getBlue() == 255 && gsColor.getAlpha() == 32) {
                color = new Color(255, 255, 255);
            }

            if (color.getAlpha() < 4) {
                color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
            }

            if (shadow) {
                color = new Color(color.getRed() / 4, color.getGreen() / 4, color.getBlue() / 4, color.getAlpha());
            }

            CFont.CharData[] currentData = this.charData;
            boolean randomCase = false;
            boolean bold = false;
            boolean italic = false;
            boolean strikethrough = false;
            boolean underline = false;
            boolean render = true;

            x *= 2.0D;
            y *= 2.0D;
            if (render) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.5D, 0.5D, 0.5D);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.color((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F);
                int size = text.length();

                GlStateManager.enableTexture2D();
                GlStateManager.bindTexture(this.tex.getGlTextureId());

                for (int i = 0; i < size; ++i) {
                    char character = text.charAt(i);

                    if (character == 167 && i < size) {
                        int colorIndex = 21;

                        try {
                            colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                        } catch (Exception exception) {
                            ;
                        }

                        if (colorIndex < 16) {
                            bold = false;
                            italic = false;
                            randomCase = false;
                            underline = false;
                            strikethrough = false;
                            GlStateManager.bindTexture(this.tex.getGlTextureId());
                            currentData = this.charData;
                            if (colorIndex < 0 || colorIndex > 15) {
                                colorIndex = 15;
                            }

                            if (shadow) {
                                colorIndex += 16;
                            }

                            int colorcode = this.colorCode[colorIndex];

                            GlStateManager.color((float) (colorcode >> 16 & 255) / 255.0F, (float) (colorcode >> 8 & 255) / 255.0F, (float) (colorcode & 255) / 255.0F, (float) color.getAlpha());
                        } else if (colorIndex == 16) {
                            randomCase = true;
                        } else if (colorIndex == 17) {
                            bold = true;
                            if (italic) {
                                GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                                currentData = this.boldItalicChars;
                            } else {
                                GlStateManager.bindTexture(this.texBold.getGlTextureId());
                                currentData = this.boldChars;
                            }
                        } else if (colorIndex == 18) {
                            strikethrough = true;
                        } else if (colorIndex == 19) {
                            underline = true;
                        } else if (colorIndex == 20) {
                            italic = true;
                            if (bold) {
                                GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                                currentData = this.boldItalicChars;
                            } else {
                                GlStateManager.bindTexture(this.texItalic.getGlTextureId());
                                currentData = this.italicChars;
                            }
                        } else if (colorIndex == 21) {
                            bold = false;
                            italic = false;
                            randomCase = false;
                            underline = false;
                            strikethrough = false;
                            GlStateManager.color((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F);
                            GlStateManager.bindTexture(this.tex.getGlTextureId());
                            currentData = this.charData;
                        }

                        ++i;
                    } else if (character < currentData.length && character >= 0) {
                        GlStateManager.glBegin(4);
                        this.drawChar(currentData, character, (float) x, (float) y);
                        GlStateManager.glEnd();
                        if (strikethrough) {
                            this.drawLine(x, y + (double) (currentData[character].height / 2), x + (double) currentData[character].width - 8.0D, y + (double) (currentData[character].height / 2), 1.0F);
                        }

                        if (underline) {
                            this.drawLine(x, y + (double) currentData[character].height - 2.0D, x + (double) currentData[character].width - 8.0D, y + (double) currentData[character].height - 2.0D, 1.0F);
                        }

                        x += (double) (currentData[character].width - 8 + this.charOffset);
                    }
                }

                GL11.glHint(3155, 4352);
                GlStateManager.popMatrix();
            }

            return (float) x / 2.0F;
        }
    }

    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        } else {
            int width = 0;
            CFont.CharData[] currentData = this.charData;
            boolean bold = false;
            boolean italic = false;
            int size = text.length();

            for (int i = 0; i < size; ++i) {
                char character = text.charAt(i);

                if (character == 167 && i < size) {
                    int colorIndex = "0123456789abcdefklmnor".indexOf(character);

                    if (colorIndex < 16) {
                        bold = false;
                        italic = false;
                    } else if (colorIndex == 17) {
                        bold = true;
                        if (italic) {
                            currentData = this.boldItalicChars;
                        } else {
                            currentData = this.boldChars;
                        }
                    } else if (colorIndex == 20) {
                        italic = true;
                        if (bold) {
                            currentData = this.boldItalicChars;
                        } else {
                            currentData = this.italicChars;
                        }
                    } else if (colorIndex == 21) {
                        bold = false;
                        italic = false;
                        currentData = this.charData;
                    }

                    ++i;
                } else if (character < currentData.length && character >= 0) {
                    width += currentData[character].width - 8 + this.charOffset;
                }
            }

            return width / 2;
        }
    }

    public void setFont(Font font) {
        super.setFont(font);
        this.setupBoldItalicIDs();
    }

    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        this.setupBoldItalicIDs();
    }

    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        this.setupBoldItalicIDs();
    }

    private void setupBoldItalicIDs() {
        this.texBold = this.setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
        this.texItalic = this.setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
        this.texItalicBold = this.setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
    }

    private void drawLine(double x, double y, double x1, double y1, float width) {
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(width);
        GlStateManager.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GlStateManager.glEnd();
        GlStateManager.enableTexture2D();
    }

    public List wrapWords(String text, double width) {
        ArrayList finalWords = new ArrayList();

        if ((double) this.getStringWidth(text) > width) {
            String[] words = text.split(" ");
            String currentWord = "";
            char lastColorCode = '\uffff';
            String[] astring = words;
            int s = words.length;

            for (int i = 0; i < s; ++i) {
                String word = astring[i];

                for (int i = 0; i < word.toCharArray().length; ++i) {
                    char c = word.toCharArray()[i];

                    if (c == 167 && i < word.toCharArray().length - 1) {
                        lastColorCode = word.toCharArray()[i + 1];
                    }
                }

                if ((double) this.getStringWidth(currentWord + word + " ") < width) {
                    currentWord = currentWord + word + " ";
                } else {
                    finalWords.add(currentWord);
                    currentWord = "§" + lastColorCode + word + " ";
                }
            }

            if (currentWord.length() > 0) {
                if ((double) this.getStringWidth(currentWord) < width) {
                    finalWords.add("§" + lastColorCode + currentWord + " ");
                    currentWord = "";
                } else {
                    Iterator iterator = this.formatString(currentWord, width).iterator();

                    while (iterator.hasNext()) {
                        String s = (String) iterator.next();

                        finalWords.add(s);
                    }
                }
            }
        } else {
            finalWords.add(text);
        }

        return finalWords;
    }

    public List formatString(String string, double width) {
        ArrayList finalWords = new ArrayList();
        String currentWord = "";
        char lastColorCode = '\uffff';
        char[] chars = string.toCharArray();

        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];

            if (c == 167 && i < chars.length - 1) {
                lastColorCode = chars[i + 1];
            }

            if ((double) this.getStringWidth(currentWord + c) < width) {
                currentWord = currentWord + c;
            } else {
                finalWords.add(currentWord);
                currentWord = "§" + lastColorCode + c;
            }
        }

        if (currentWord.length() > 0) {
            finalWords.add(currentWord);
        }

        return finalWords;
    }

    private void setupMinecraftColorcodes() {
        for (int index = 0; index < 32; ++index) {
            int noClue = (index >> 3 & 1) * 85;
            int red = (index >> 2 & 1) * 170 + noClue;
            int green = (index >> 1 & 1) * 170 + noClue;
            int blue = (index >> 0 & 1) * 170 + noClue;

            if (index == 6) {
                red += 85;
            }

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            this.colorCode[index] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
        }

    }
}
