using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class CounterScript : MonoBehaviour {

    public int gold;
    public int nexus_health = 100;

	public Text coinCounterText;

    public int max_wave = 10;
    private int wave = 0;


	// Use this for initialization
	void Start () {
		SetCoinCounterText ();
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public bool cash(int x)
    {
        if (gold + x < 0) { return false; }
        gold += x;
		SetCoinCounterText ();
        return true; 
    }

    public void DamageToNexus(int x)
    {
        nexus_health -= x;
        if (nexus_health <= 0) {
            GameOver(false);
        }
    }

    public void NextWave()
    {
        if (wave <= max_wave)
            wave += 1;
        else GameOver(true);//?
    }

    void GameOver(bool is_Victory)
    {

    }

	void SetCoinCounterText() {
		coinCounterText.text = gold.ToString ();
	}
}
