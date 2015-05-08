from deck import Deck
from player import Player

class Dealer(object):

  def __init__(self, players):
      self.deck = Deck()
      self.players = players
      self.me = Player(0)
      self.players.append(self.me)
      # dealer starts with 1000 coins so that he can x out the winnings
      self.bank = 1000
      
  def clear_hands(self):
      for num in range(0, len(self.players)-1):
          player_name = 'Player '+str(num+1)
          myplayer = self.get_player(num)
          myplayer.clear_hand()
      self.get_me().clear_hand()
      
  def shuffle(self):
      self.deck.shuffle()
      
  def deal(self):
      for num in range(0,2):
          for player in self.players:
              player.push_card(self.deck.pop_card())
      return self.show_cards(False)
  
  def get_me(self):
    return self.me
  
  def get_player(self, num):
    if num <= len(self.players)-1: 
       return self.players[num]
    else:
        raise ValueError,"an invalid player was requested"
        
  def draw(self, player):
      card = self.deck.pop_card()
      player.push_card(card)
      return card
  
  def remove_player(self, num):
      self.players.remove(num)
  
  def pay(self, bet, is_blackjack):
      #print '###### in pay BET: '+str(bet)+" blackjack: " +str(is_blackjack)
      if is_blackjack == True:
          win = bet * 1.5  + bet
      else:
          win = bet + bet
      self.bank -= win
      
      return win
  
  def add_to_bank(self, coins):
      self.bank += coins
      

  def show_cards(self, show_dealer):
      player_num = 1
      cards = []
      for player in self.players:
          # make sure that the player is not the dealer 
          if player != self.me :
              cards.append('PLAYER: '+str(player_num) +"; "+str(player))
              player_num += 1
          else:
              if show_dealer :
                  cards.append('DEALER ; '+str(player))
              else: 
                  cards.append("DEALER;  CARDS: (hidden), "+player.get_card(1))
          
      return "\n".join(cards)
       