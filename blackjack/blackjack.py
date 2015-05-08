import sys
from sys import stdin
from sys import stdout
from dealer import Dealer
from player import Player


def str_players(players):
    strs = []
    for player in players:
      strs.append(str(player))
      
    return '\n'.join(strs)
    
def run_game(num_players, dealer):
    
    dealer.clear_hands()
             
    # Gather the bets
    for num in range(0, int(num_players)):
        print 'Enter the bet for Player '+ str(num+1) 
        bet = stdin.readline()
        player = dealer.get_player(num)
        player.set_bet(int(bet))
              
             
    # deal the cards
    print 'press enter key to deal the cards'
    deal = stdin.readline()
    dealer.deal()
    print dealer.show_cards(False)+"\n"
    
    num_busted_players = 0
    
    # handle players
    for num in range(0, int(num_players)):
        player_name = 'Player '+str(num+1)
        print '\n'+player_name+"'s turn"

        while True:
            player = dealer.get_player(num)
            sum = player.get_sum()
            
            if sum == 21 :
                print player_name+' has BLACKJACK.'
                break  
            elif sum > 21 :
                num_busted_players += 1 
                print player_name+ ' is busted. Continuing ...'
                #dealer.remove_player(num)
                break
      
            print "    Sum is: "+str(sum)
            print "    Enter HIT or STAND "
            cmd = str(stdin.readline().strip())
            
            if cmd == "HIT":
                card = dealer.draw(player)
                sum = player.get_sum()
                print "    Card: "+str(card)
                continue
            elif cmd == 'STAND':
                break
            else:
                print "    Unknown Command."
                continue
        
    # now handle the dealer
    dealer_hand = dealer.get_me()
    dealer_hand_sum = dealer_hand.get_sum()
    print "\nDEALER: "+dealer_hand.show_cards()
    
    if num_players == num_busted_players :
        print "All players are busted. Adding all bets to bank"
        for num in range(0, int(num_players)):
            myplayer = dealer.get_player(num)
            dealer.add_to_bank(myplayer.get_bet())
    else :
        # this loop is for drawing the cards for the dealer
        while True:
            # handle the soft 17. If the dealer has 17 he needs to draw
            if dealer_hand_sum > 17 and dealer_hand_sum <= 20 :
                print "DEALER STANDS at "+str(dealer_hand_sum)
                break
            if dealer_hand_sum == 21 :
                print "DEALER has BLACKJACK!!!"
                break
            elif dealer_hand_sum > 21 :
                print "DEALER is busted!!"
                break
            else :            
                card = dealer.draw(dealer_hand)
                dealer_hand_sum = dealer_hand.get_sum()
                print "    "+str(card)
                print "    Sum is: " + str(dealer_hand_sum)
                continue
                
        for num in range(0, int(num_players)):
            myplayer = dealer.get_player(num)
            myplayer_sum = myplayer.get_sum()
            myplayer_name = "Player "+str(num+1)
            
            if myplayer_sum > 21 :
                # player is busted. continue
                continue
            if myplayer_sum == 21 and dealer_hand_sum != 21 :
                pay = dealer.pay(myplayer.get_bet(), myplayer_sum == 21)
                myplayer.add_to_wallet(pay)
                print myplayer_name+" has BLACKJACK. Paying out - "+ str(pay)
            elif dealer_hand_sum > 21 :
                pay = dealer.pay(myplayer.get_bet(), myplayer_sum == 21)
                myplayer.add_to_wallet(pay)
                print "DEALER is busted. So paying out 1:1 - "+str(pay)
            elif myplayer_sum == dealer_hand_sum :
                print "DEALER and "+myplayer_name+" are drawn. Returning the bet - "+str(myplayer.get_bet())
                myplayer.add_to_wallet(myplayer.get_bet())
            elif myplayer_sum < dealer_hand_sum :
                print myplayer_name+" lost the bet to DEALER"
                dealer.add_to_bank(dealer_hand.get_bet())
            else :
                pay = dealer.pay(myplayer.get_bet(), myplayer_sum == 21)
                myplayer.add_to_wallet(pay)
                print myplayer_name+" beats the DEALER. Paying out - "+str(pay)
     
def run():

    print "Starting the game. Enter the number of players"
    num_players = stdin.readline()
    players =[]
    
    # create the set of players
    for num in range(int(num_players)):
      player =  Player(0)
      players.append(player)
      
    dealer = Dealer(players)

    print 'Press enter key to shuffle the cards.'
    shuffle = stdin.readline()
    dealer.shuffle()
        
    run_game(num_players, dealer)
  
    while True:
      print "\nPlay another game? YES/NO"
      cmd = str(stdin.readline().strip())
      if cmd == 'YES':
          run_game(num_players, dealer)
          continue
      elif cmd == 'NO':
          break
      else:
          print "Unknown command "+cmd
          continue
      

#print  str_players(players)

if __name__ == "__main__":
    run()