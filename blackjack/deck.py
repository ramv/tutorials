from card import Card
import random

class Deck(object):

  # constructor     
  def __init__(self):
    self.cards = []
    for suit in range(4):
        for rank in range(1, 14):
            card = Card(suit, rank)
            self.cards.append(card)
  
  # to string method
  def __str__(self):
    res = []
    for card in self.cards:
      res.append(str(card))
    return '\n'.join(res)
  
  # method to shuffle the cards
  def shuffle(self):
     random.shuffle(self.cards);
     
  def pop_card(self):
      if len(self.cards) > 0 :
          return self.cards.pop()
      else:
          raise IndexError, 'No more cards left in the deck'
      